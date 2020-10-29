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
import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalCustomer;
import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalDiscrepancyDto;
import com.purnima.jain.consistency.checker.util.Util;

@Service
public class CustomerMainDetailsComparisonService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerMainDetailsComparisonService.class);

	public List<ConsistencyCheckerInternalDiscrepancyDto> compare(ConsistencyCheckerInternalCustomer internalCustomerMySql, ConsistencyCheckerInternalCustomer internalCustomerCassandra) throws JsonProcessingException {
		logger.debug("Entering CustomerMainDetailsComparisonService.compare().......");

		List<ConsistencyCheckerInternalDiscrepancyDto> consistencyCheckerInternalDiscrepancyDtoListForCustomerMainDetails = new ArrayList<>();

		Boolean isCustomerMainDetailsEqual = ConsistencyCheckerInternalCustomer.isCustomerMainDetailsEqual(internalCustomerMySql, internalCustomerCassandra);

		if (!isCustomerMainDetailsEqual) {
			ConsistencyCheckerInternalDiscrepancyDto consistencyCheckerInternalDiscrepancyDto = new ConsistencyCheckerInternalDiscrepancyDto();
			consistencyCheckerInternalDiscrepancyDto.setId(UUID.randomUUID());
			consistencyCheckerInternalDiscrepancyDto.setInconsistencyType(InconsistencyTypeEnum.CUSTOMER_MAIN_DETAILS);
			consistencyCheckerInternalDiscrepancyDto.setMySqlContents(Util.convertObjectToJson(internalCustomerMySql));
			consistencyCheckerInternalDiscrepancyDto.setCassandraContents(Util.convertObjectToJson(internalCustomerCassandra));
			consistencyCheckerInternalDiscrepancyDto.setReconciliationStatus(ReconciliationStatusEnum.UNRECONCILED);

			consistencyCheckerInternalDiscrepancyDtoListForCustomerMainDetails.add(consistencyCheckerInternalDiscrepancyDto);
		}

		logger.debug("Leaving CustomerMainDetailsComparisonService.compare().......");
		return consistencyCheckerInternalDiscrepancyDtoListForCustomerMainDetails;
	}

}
