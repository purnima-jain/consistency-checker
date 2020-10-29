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
import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalPhone;
import com.purnima.jain.consistency.checker.util.Util;

@Service
public class PhonesComparisonService {

	private static final Logger logger = LoggerFactory.getLogger(PhonesComparisonService.class);

	public List<ConsistencyCheckerInternalDiscrepancyDto> compare(List<ConsistencyCheckerInternalPhone> phonesMySqlList, List<ConsistencyCheckerInternalPhone> phonesCassandraList) throws JsonProcessingException {
		logger.debug("Entering PhonesComparisonService.compare().......");

		List<ConsistencyCheckerInternalDiscrepancyDto> consistencyCheckerInternalDiscrepancyDtoListForPhones = new ArrayList<>();

		Boolean isPhonesEqual = ConsistencyCheckerInternalPhone.compareLists(phonesMySqlList, phonesCassandraList);

		if (!isPhonesEqual) {
			ConsistencyCheckerInternalDiscrepancyDto consistencyCheckerInternalDiscrepancyDto = new ConsistencyCheckerInternalDiscrepancyDto();
			consistencyCheckerInternalDiscrepancyDto.setId(UUID.randomUUID());
			consistencyCheckerInternalDiscrepancyDto.setInconsistencyType(InconsistencyTypeEnum.PHONES);
			consistencyCheckerInternalDiscrepancyDto.setMySqlContents(Util.convertObjectToJson(phonesMySqlList));
			consistencyCheckerInternalDiscrepancyDto.setCassandraContents(Util.convertObjectToJson(phonesCassandraList));
			consistencyCheckerInternalDiscrepancyDto.setReconciliationStatus(ReconciliationStatusEnum.UNRECONCILED);

			consistencyCheckerInternalDiscrepancyDtoListForPhones.add(consistencyCheckerInternalDiscrepancyDto);
		}

		return consistencyCheckerInternalDiscrepancyDtoListForPhones;
	}

}