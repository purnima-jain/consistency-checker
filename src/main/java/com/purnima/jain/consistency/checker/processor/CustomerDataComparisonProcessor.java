package com.purnima.jain.consistency.checker.processor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalDiscrepancyDto;
import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalDto;
import com.purnima.jain.consistency.checker.service.CustomerMainDetailsComparisonService;
import com.purnima.jain.consistency.checker.service.EmailsComparisonService;
import com.purnima.jain.consistency.checker.service.PhonesComparisonService;

@Component
@StepScope
public class CustomerDataComparisonProcessor implements ItemProcessor<ConsistencyCheckerInternalDto, ConsistencyCheckerInternalDto> {

	private static final Logger logger = LoggerFactory.getLogger(CustomerDataComparisonProcessor.class);

	@Value("#{stepExecution}")
	private StepExecution stepExecution;

	@Autowired
	private CustomerMainDetailsComparisonService customerMainDetailsComparisonService;

	@Autowired
	private PhonesComparisonService phonesComparisonService;

	@Autowired
	private EmailsComparisonService emailsComparisonService;

	@Override
	public ConsistencyCheckerInternalDto process(ConsistencyCheckerInternalDto consistencyCheckerInternalDto) throws JsonProcessingException {
		logger.debug("Entering CustomerDataComparisonProcessor.process() with consistencyCheckerInternalDto:: {}", consistencyCheckerInternalDto);
		logger.debug("Comparing data for customerId: {}", consistencyCheckerInternalDto.getCustomerId());
		Long jobExecutionId = stepExecution.getJobExecutionId();
		Long jobInstanceId = stepExecution.getJobExecution().getJobInstance().getInstanceId();
		Long stepExecutionId = stepExecution.getId();

		// For CustomerMainDetails
		List<ConsistencyCheckerInternalDiscrepancyDto> discrepancyDtoListForCustomerMainDetails = customerMainDetailsComparisonService
				.compare(consistencyCheckerInternalDto.getMySqlCustomer(), consistencyCheckerInternalDto.getCassandraCustomer());

		// For Phones
		List<ConsistencyCheckerInternalDiscrepancyDto> discrepancyDtoListForPhones = phonesComparisonService.compare(consistencyCheckerInternalDto.getMySqlCustomer().getPhones(),
				consistencyCheckerInternalDto.getCassandraCustomer().getPhones());

		// For Emails
		List<ConsistencyCheckerInternalDiscrepancyDto> discrepancyDtoListForEmails = emailsComparisonService.compare(consistencyCheckerInternalDto.getMySqlCustomer().getEmails(),
				consistencyCheckerInternalDto.getCassandraCustomer().getEmails());

		// Combining all discrepancies
		consistencyCheckerInternalDto.getDiscrepancyList().addAll(discrepancyDtoListForCustomerMainDetails);
		consistencyCheckerInternalDto.getDiscrepancyList().addAll(discrepancyDtoListForPhones);
		consistencyCheckerInternalDto.getDiscrepancyList().addAll(discrepancyDtoListForEmails);

		for (ConsistencyCheckerInternalDiscrepancyDto consistencyCheckerInternalDiscrepancyDto : consistencyCheckerInternalDto.getDiscrepancyList()) {
			consistencyCheckerInternalDiscrepancyDto.setJobExecutionId(jobExecutionId);
			consistencyCheckerInternalDiscrepancyDto.setJobInstanceId(jobInstanceId);
			consistencyCheckerInternalDiscrepancyDto.setStepExecutionId(stepExecutionId);
		}

		logger.debug("Leaving CustomerDataComparisonProcessor.process() with consistencyCheckerInternalDto:: {}", consistencyCheckerInternalDto);
		return consistencyCheckerInternalDto;
	}

}
