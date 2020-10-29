package com.purnima.jain.consistency.checker.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.purnima.jain.consistency.checker.postgres.repo.SpringBatchRepository;
import com.purnima.jain.consistency.checker.util.Constants;

import io.swagger.annotations.ApiOperation;

@RestController
public class ConsistencyCheckerController {

	private static final Logger logger = LoggerFactory.getLogger(ConsistencyCheckerController.class);

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SSSSSS");

	private static final String defaultFromTimestampAsString = "2000-01-01T00:00:00.000000";
	private static final String defaultToTimestampAsString = "2099-12-31T23:59:59.999999";
	
	@Autowired
	private SpringBatchRepository springBatchRepository;
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	@Qualifier("customerConsistencyCheckerJob")
	private Job customerConsistencyCheckerJob;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private JobOperator jobOperator;
	
	@Autowired
	private JobExplorer jobExplorer;
	
	@PostMapping(value = "/consistency-checker/invokeforperiod", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Checks for the consistency of any records that changed in the defined period; default start time is beginning of yesterday, default end time is end of century", response = String.class)
	public String invokeForPeriod(@RequestParam(required = false, name = "from", defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
			@RequestParam(required = false, name = "to", defaultValue = defaultToTimestampAsString) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) throws Exception {

		logger.debug("Entering ConsistencyCheckerController.invokeForPeriod() with parameters::\n from: {}\n to: {}\n", from, to);
		
		from = initializeFrom(from);
		to = initializeTo(to);		

		logger.info("Final Formatted Dates::\n from:: {}\n to:: {}\n", dateTimeFormatter.format(from), dateTimeFormatter.format(to));
		
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("from", dateTimeFormatter.format(from))
				.addString("to", dateTimeFormatter.format(to))
				.toJobParameters();
		
		JobExecution jobExecution = executeJob(jobParameters);

		logger.debug("Leaving ConsistencyCheckerController.invokeForPeriod() with jobExecution.getStatus():: {}", jobExecution.getStatus());

		return "{\"message\": \"Batch job has finished:" + jobExecution.getStatus() + "\"}";
	}
	
	private LocalDateTime initializeFrom(LocalDateTime from) {
		if (from == null) {
			// from = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0); // Beginning of yesterday
			from = LocalDateTime.parse(defaultFromTimestampAsString, DateTimeFormatter.ISO_LOCAL_DATE_TIME); // Beginning of century
		}
		return from;
	}	
	
	private LocalDateTime initializeTo(LocalDateTime to) {
		if (to == null) {
			to = LocalDateTime.parse(defaultToTimestampAsString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		}
		return to;
	}
	
	private JobExecution executeJob(JobParameters jobParameters) throws Exception {
		logger.debug("Entering ConsistencyCheckerController.executeJob() with parameters:: {}", jobParameters);
		
		JobExecution jobExecution = null;
		
		// Get the last run.counter from BATCH_JOB_EXECUTION_PARAMS table for the given Job Parameters
		Long maxRunCounterYetForGivenParameters = getMaxRunCounterYetForGivenParameters(jobParameters);
		logger.info("maxRunCounterYetForGivenParameters :: {}", maxRunCounterYetForGivenParameters);
		
		// If the last run.counter is null
		if(maxRunCounterYetForGivenParameters == null) {
			logger.debug("Job with the given parameters is scheduled for the first time ever.....");
			jobExecution = getFirstJobExecutionForGivenParameters(jobParameters);				
		}
		else {
			logger.debug("Job with the given parameters has been executed before.....");
			JobExecution lastJobExecution = getLastJobExecutionForGivenParametersAndRunCounter(jobParameters, maxRunCounterYetForGivenParameters);
			
			if(ExitStatus.COMPLETED.getExitCode().equals(lastJobExecution.getExitStatus().getExitCode())) {
				logger.debug("The previous execution of the Job with the given parameters was successful, hence we need to increase the run.counter.....");				
				jobExecution = getNewFreshJobExecutionForGivenParametersWithNextRunCounter(jobParameters, maxRunCounterYetForGivenParameters);
			}
			else if(ExitStatus.FAILED.getExitCode().equals(lastJobExecution.getExitStatus().getExitCode())) {
				logger.debug("The previous execution of the Job with the given parameters was a failure, hence we need to re-start it's processing.....");
				jobExecution = getRestartJobExecutionForGivenParametersForTheLastFailedJob(lastJobExecution);
			}
			else {
				logger.error("ERROR!!! Un-expected exit status of previous job: {}", lastJobExecution.getExitStatus().toString());
				throw new Exception("Un-expected exit status of previous job: " + lastJobExecution.getExitStatus().toString());
			}
		}
		return jobExecution;		
	}
	
	private Long getMaxRunCounterYetForGivenParameters(JobParameters jobParameters) {
		Long maxRunCounterForGivenParameters = springBatchRepository.getMaxRunCounterForGivenParameters(jobParameters);		
		return maxRunCounterForGivenParameters;
	}
	
	private JobExecution getFirstJobExecutionForGivenParameters(JobParameters jobParameters) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		// Execute a first new start with run.counter = 1 for the given parameters
		JobParameters jobParametersAfterAppendingRunCounter = new JobParametersBuilder().addJobParameters(jobParameters).addLong(Constants.RUN_COUNTER_ATTRIBUTE_NAME, 1L).toJobParameters();
		JobExecution jobExecution = jobLauncher.run(customerConsistencyCheckerJob, jobParametersAfterAppendingRunCounter);
		return jobExecution;
	}
	
	private JobExecution getLastJobExecutionForGivenParametersAndRunCounter(JobParameters jobParameters, Long maxRunCounterYetForGivenParameters) {
		// Get the last Job Execution for the given parameters and run.counter
		JobParameters jobParametersAfterAppendingRunCounter = new JobParametersBuilder().addJobParameters(jobParameters).addLong(Constants.RUN_COUNTER_ATTRIBUTE_NAME, maxRunCounterYetForGivenParameters).toJobParameters();
		JobExecution lastJobExecution = jobRepository.getLastJobExecution(customerConsistencyCheckerJob.getName(), jobParametersAfterAppendingRunCounter);
		return lastJobExecution;
	}
	
	private JobExecution getNewFreshJobExecutionForGivenParametersWithNextRunCounter(JobParameters jobParameters, Long maxRunCounterYetForGivenParameters) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		logger.debug("Fresh start of the job after successful completion of the previous one with the same parameters: {}", customerConsistencyCheckerJob.getName());
		// Configure the new job with incremented run.counter
		JobParameters jobParametersAfterIncrementingRunCounter = new JobParametersBuilder().addJobParameters(jobParameters).addLong(Constants.RUN_COUNTER_ATTRIBUTE_NAME, ++maxRunCounterYetForGivenParameters).toJobParameters();
		logger.debug("jobParametersAfterIncrementingRunCounter :: {}", jobParametersAfterIncrementingRunCounter);
		JobExecution jobExecution = jobLauncher.run(customerConsistencyCheckerJob, jobParametersAfterIncrementingRunCounter);
		return jobExecution;
	}
	
	private JobExecution getRestartJobExecutionForGivenParametersForTheLastFailedJob(JobExecution lastJobExecution) throws JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, NoSuchJobException, JobRestartException, JobParametersInvalidException {
		// Execute the job with the failed lastJobExecution.id
		logger.debug("Restart of job after failed completion of the previous run: {}", customerConsistencyCheckerJob.getName());
		Long restartId = jobOperator.restart(lastJobExecution.getId());
		logger.debug("restartId:: {}", restartId);
		JobExecution jobExecution = jobExplorer.getJobExecution(restartId);
		return jobExecution;
	}	

	@Scheduled(cron="${cron.expression.everyday.at.midnight}", zone="CET")
	public void invokeForPeriodOnSchedule() throws Exception {
		logger.debug("Entering ConsistencyCheckerController.invokeForPeriodOnSchedule()..............................................");
		invokeForPeriod(null, null);
		logger.debug("Leaving ConsistencyCheckerController.invokeForPeriodOnSchedule()...............................................");
	}	

}
