package com.purnima.jain.consistency.checker.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalDto;
import com.purnima.jain.consistency.checker.postgres.entity.CustomerConsistencyCheckerJobSelectionEntity;
import com.purnima.jain.consistency.checker.processor.CustomerDataComparisonProcessor;
import com.purnima.jain.consistency.checker.processor.CustomerIdSelectionProcessor;
import com.purnima.jain.consistency.checker.reader.CompositeJdbcPagingItemReader;
import com.purnima.jain.consistency.checker.reader.CustomerIdSelectionReader;
import com.purnima.jain.consistency.checker.reader.SelectedCustomerIdReader;
import com.purnima.jain.consistency.checker.writer.CustomerDataComparisonResultWriter;
import com.purnima.jain.consistency.checker.writer.CustomerIdSelectionWriter;

/**
 * This is the main Job class that controls two other child jobs.
 * The JobLauncher will launch this class and then this will control the execution of the child jobs.
 *
 */
@Configuration
@EnableBatchProcessing
public class CustomerConsistencyCheckerJobConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomerConsistencyCheckerJobConfig.class);
	
	@Value("${chunk_size}")
	private Integer chunkSize;
	
	// First Step - Reader
	@Autowired
	private CustomerIdSelectionReader customerIdSelectionReader;
	
	// First Step - Processor
	@Autowired
	private CustomerIdSelectionProcessor customerIdSelectionProcessor;
	
	// First Step - Writer
	@Autowired
	private CustomerIdSelectionWriter customerIdSelectionWriter;
	
	// Second Step - Reader
	@Autowired
	private SelectedCustomerIdReader selectedCustomerIdReader;
	
	// Second Step - Processor
	@Autowired
	private CustomerDataComparisonProcessor customerDataComparisonProcessor;
	
	// Second Step - Writer
	@Autowired
	private CustomerDataComparisonResultWriter customerDataComparisonResultWriter;
	
	
	@Autowired
	@Qualifier("mySqlDataSource")
	private DataSource mySqlDataSource;
	
	@Autowired
	private JobRegistry jobRegistry;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	// Entire Job Configuration
	@Bean(name = "customerConsistencyCheckerJob")
	public Job customerConsistencyCheckerJob() throws DuplicateJobException {
		logger.debug("Entering CustomerConsistencyCheckerJob.customerConsistencyCheckerJob()...............");
		
		Job job = jobBuilderFactory.get("customerConsistencyCheckerJob")
				.start(customerIdSelection())
				.next(performDataRetrievalAndComparison())
				.build();
		
		ReferenceJobFactory referenceJobFactory = new ReferenceJobFactory(job);
		jobRegistry.register(referenceJobFactory);
		
		return job;		
	}
	
	// First Step - Configuration
	@Bean
	@JobScope
	public Step customerIdSelection() {
		logger.debug("Entering CustomerConsistencyCheckerJob.customerIdSelection()...............");
		return stepBuilderFactory.get("customerIdSelectionStep")
				.<String, CustomerConsistencyCheckerJobSelectionEntity> chunk(chunkSize)
				.reader(getCustomerIdSelectionReader(null, null))
				.processor(customerIdSelectionProcessor)
				.writer(customerIdSelectionWriter)
				.build();
	}
	
	// First Step - Reader
	@Bean(destroyMethod = "")
	@JobScope
	public JdbcCursorItemReader<String> getCustomerIdSelectionReader(@Value("#{jobParameters['from']}") String from, @Value("#{jobParameters['to']}") String to) {
		logger.debug("Entering CustomerConsistencyCheckerJob.getCustomerIdSelectionReader() with::\n from: {}\n to:: {}\n", from, to);		
		return customerIdSelectionReader.getCustomerIdSelectionReader(from, to);
	}
	
	// Second Step - Configuration
	@Bean
	@JobScope
	public Step performDataRetrievalAndComparison() {
		logger.debug("Entering CustomerConsistencyCheckerJob.performDataRetrievalAndComparison()...............");
		return stepBuilderFactory.get("performDataRetrievalAndComparisonStep")
				.<ConsistencyCheckerInternalDto, ConsistencyCheckerInternalDto> chunk(chunkSize)
				.reader(getSelectedCustomerIdReader(null))
				.processor(customerDataComparisonProcessor)
				.writer(customerDataComparisonResultWriter)
				.build();
	}
	
	// Second Step - Reader
	@Bean(destroyMethod = "")
	@StepScope
	public CompositeJdbcPagingItemReader<ConsistencyCheckerInternalDto> getSelectedCustomerIdReader(@Value("#{stepExecution}") StepExecution stepExecution) {
		logger.debug("Entering CustomerConsistencyCheckerJob.getSelectedCustomerIdReader()...............");
		return selectedCustomerIdReader.getSelectedCustomerIdReader(stepExecution.getJobExecution().getJobInstance().getInstanceId());
	}

}
