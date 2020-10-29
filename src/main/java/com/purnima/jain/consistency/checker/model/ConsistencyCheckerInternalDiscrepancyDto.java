package com.purnima.jain.consistency.checker.model;

import java.util.UUID;

import com.purnima.jain.consistency.checker.enums.InconsistencyTypeEnum;
import com.purnima.jain.consistency.checker.enums.ReconciliationStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsistencyCheckerInternalDiscrepancyDto {

	private UUID id;
	private String customerId;
	private InconsistencyTypeEnum inconsistencyType;
	private String mySqlContents;
	private String cassandraContents;
	private ReconciliationStatusEnum reconciliationStatus;

	private Long jobExecutionId;
	private Long jobInstanceId;
	private Long stepExecutionId;

}
