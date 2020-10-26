package com.purnima.jain.consistency.checker.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsistencyCheckerInternalDiscrepancyDto {
	
	private UUID id;
	private String customerId;
	private String inconsistencyType;
	private String mySqlContents;
	private String cassandraContents;
	private String reconciliationStatus;
	
	private Long jobExecutionId;
	private Long jobInstanceId;
	private Long stepExecutionId;
	
	

}
