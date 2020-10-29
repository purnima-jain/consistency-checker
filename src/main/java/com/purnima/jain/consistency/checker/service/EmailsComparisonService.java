package com.purnima.jain.consistency.checker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.purnima.jain.consistency.checker.enums.InconsistencyTypeEnum;
import com.purnima.jain.consistency.checker.enums.ReconciliationStatusEnum;
import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalDiscrepancyDto;
import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalEmail;
import com.purnima.jain.consistency.checker.util.Util;

@Service
public class EmailsComparisonService {

	private static final Logger logger = LoggerFactory.getLogger(EmailsComparisonService.class);

	public List<ConsistencyCheckerInternalDiscrepancyDto> compare(List<ConsistencyCheckerInternalEmail> emailsMySqlList, List<ConsistencyCheckerInternalEmail> emailsCassandraList) throws JsonProcessingException {
		logger.debug("Entering EmailsComparisonService.compare().......");

		List<ConsistencyCheckerInternalDiscrepancyDto> consistencyCheckerInternalDiscrepancyDtoListForEmails = new ArrayList<>();

		Boolean isEmailsEqual = ConsistencyCheckerInternalEmail.compareLists(emailsMySqlList, emailsCassandraList);

		if (!isEmailsEqual) {
			ConsistencyCheckerInternalDiscrepancyDto consistencyCheckerInternalDiscrepancyDto = new ConsistencyCheckerInternalDiscrepancyDto();
			consistencyCheckerInternalDiscrepancyDto.setId(UUID.randomUUID());
			consistencyCheckerInternalDiscrepancyDto.setInconsistencyType(InconsistencyTypeEnum.EMAILS);
			consistencyCheckerInternalDiscrepancyDto.setMySqlContents(Util.convertObjectToJson(emailsMySqlList));
			consistencyCheckerInternalDiscrepancyDto.setCassandraContents(Util.convertObjectToJson(emailsCassandraList));
			consistencyCheckerInternalDiscrepancyDto.setReconciliationStatus(ReconciliationStatusEnum.UNRECONCILED);

			consistencyCheckerInternalDiscrepancyDtoListForEmails.add(consistencyCheckerInternalDiscrepancyDto);
		}

		return consistencyCheckerInternalDiscrepancyDtoListForEmails;
	}

}