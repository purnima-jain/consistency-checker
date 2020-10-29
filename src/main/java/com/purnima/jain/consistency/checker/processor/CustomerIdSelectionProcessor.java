package com.purnima.jain.consistency.checker.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.purnima.jain.consistency.checker.enums.ProcessingStatusEnum;
import com.purnima.jain.consistency.checker.postgres.entity.CustomerConsistencyCheckerJobSelectionEntity;

@Component
@StepScope
public class CustomerIdSelectionProcessor implements ItemProcessor<String, CustomerConsistencyCheckerJobSelectionEntity> {

	private static final Logger logger = LoggerFactory.getLogger(CustomerIdSelectionProcessor.class);

	@Value("#{stepExecution}")
	private StepExecution stepExecution;

	@Override
	public CustomerConsistencyCheckerJobSelectionEntity process(String customerId) throws Exception {
		logger.debug("Entering CustomerIdSelectionProcessor.process() with customerId:: {}", customerId);

		CustomerConsistencyCheckerJobSelectionEntity customerConsistencyCheckerJobSelectionEntity = new CustomerConsistencyCheckerJobSelectionEntity();
		customerConsistencyCheckerJobSelectionEntity.setCustomerId(customerId);
		customerConsistencyCheckerJobSelectionEntity.setProcessingStatusEnum(ProcessingStatusEnum.UNPROCESSED);

		customerConsistencyCheckerJobSelectionEntity.setJobExecutionId(stepExecution.getJobExecutionId());
		customerConsistencyCheckerJobSelectionEntity.setJobInstanceId(stepExecution.getJobExecution().getJobInstance().getInstanceId());
		customerConsistencyCheckerJobSelectionEntity.setStepExecutionId(stepExecution.getId());

		logger.debug("Leaving CustomerIdSelectionProcessor.process() with customerConsistencyCheckerJobSelectionEntity:: {}", customerConsistencyCheckerJobSelectionEntity);
		return customerConsistencyCheckerJobSelectionEntity;
	}

}
