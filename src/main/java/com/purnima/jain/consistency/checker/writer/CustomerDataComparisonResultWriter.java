package com.purnima.jain.consistency.checker.writer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.purnima.jain.consistency.checker.enums.ProcessingStatusEnum;
import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalDiscrepancyDto;
import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalDto;
import com.purnima.jain.consistency.checker.postgres.entity.CustomerConsistencyCheckerJobDiscrepancyEntity;
import com.purnima.jain.consistency.checker.postgres.repo.ConsistencyCheckerJobDiscrepancyRepository;
import com.purnima.jain.consistency.checker.postgres.repo.CustomerConsistencyCheckerJobSelectionRepository;

@Component
public class CustomerDataComparisonResultWriter implements ItemWriter<ConsistencyCheckerInternalDto> {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomerDataComparisonResultWriter.class);
	
	private Long jobInstanceId;
	
	@BeforeStep
	public void getInterstepData(StepExecution stepExecution) {
		JobExecution jobExecution = stepExecution.getJobExecution();
		this.jobInstanceId = jobExecution.getJobId();
	}
	
	@Autowired
	private ConsistencyCheckerJobDiscrepancyRepository consistencyCheckerJobDiscrepancyRepository;
	
	@Autowired
	private CustomerConsistencyCheckerJobSelectionRepository customerConsistencyCheckerJobSelectionRepository;

	@Override
	public void write(List<? extends ConsistencyCheckerInternalDto> consistencyCheckerInternalDtoList) throws Exception {
		logger.debug("Entering CustomerDataComparisonResultWriter.write()......");
		for(ConsistencyCheckerInternalDto consistencyCheckerInternalDto : consistencyCheckerInternalDtoList) {
			for(ConsistencyCheckerInternalDiscrepancyDto consistencyCheckerInternalDiscrepancyDto : consistencyCheckerInternalDto.getDiscrepancyList()) {
				CustomerConsistencyCheckerJobDiscrepancyEntity customerConsistencyCheckerJobDiscrepancyEntity = new CustomerConsistencyCheckerJobDiscrepancyEntity();
				customerConsistencyCheckerJobDiscrepancyEntity.setId(consistencyCheckerInternalDiscrepancyDto.getId());
				customerConsistencyCheckerJobDiscrepancyEntity.setCustomerId(consistencyCheckerInternalDto.getCustomerId());
				customerConsistencyCheckerJobDiscrepancyEntity.setInconsistencyType(consistencyCheckerInternalDiscrepancyDto.getInconsistencyType());
				customerConsistencyCheckerJobDiscrepancyEntity.setMySqlContents(consistencyCheckerInternalDiscrepancyDto.getMySqlContents());
				customerConsistencyCheckerJobDiscrepancyEntity.setCassandraContents(consistencyCheckerInternalDiscrepancyDto.getCassandraContents());
				customerConsistencyCheckerJobDiscrepancyEntity.setReconciliationStatus(consistencyCheckerInternalDiscrepancyDto.getReconciliationStatus());
				
				customerConsistencyCheckerJobDiscrepancyEntity.setJobExecutionId(consistencyCheckerInternalDiscrepancyDto.getJobExecutionId());
				customerConsistencyCheckerJobDiscrepancyEntity.setJobInstanceId(consistencyCheckerInternalDiscrepancyDto.getJobInstanceId());
				customerConsistencyCheckerJobDiscrepancyEntity.setStepExecutionId(consistencyCheckerInternalDiscrepancyDto.getStepExecutionId());
				
				consistencyCheckerJobDiscrepancyRepository.save(customerConsistencyCheckerJobDiscrepancyEntity);
			}
			customerConsistencyCheckerJobSelectionRepository.updateProcessingStatus(this.jobInstanceId, consistencyCheckerInternalDto.getCustomerId(), ProcessingStatusEnum.PROCESSED);
		}		 
	}	

}
