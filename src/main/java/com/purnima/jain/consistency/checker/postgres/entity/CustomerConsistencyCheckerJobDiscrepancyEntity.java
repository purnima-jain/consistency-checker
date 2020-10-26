package com.purnima.jain.consistency.checker.postgres.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_consistency_checker_discrepancy")
public class CustomerConsistencyCheckerJobDiscrepancyEntity {
	
	@SuppressWarnings("unused")
	private static final Long serialVersionUid = 1L;
	
	@Id
	@Column(name = "ID")
	private UUID id;
	
	@Column(name = "CUSTOMER_ID")
	private String customerId;
	
	@Column(name = "INCONSISTENCY_TYPE")
	private String inconsistencyType;
	
	@Column(name = "MYSQL_CONTENTS")
	private String mySqlContents;
	
	@Column(name = "CASSANDRA_CONTENTS")
	private String cassandraContents;
	
	@Column(name = "RECONCILIATION_STATUS")
	private String reconciliationStatus;
	
	@Column(name = "JOB_INSTANCE_ID")
	private Long jobInstanceId;
	
	@Column(name = "JOB_EXECUTION_ID")
	private Long jobExecutionId;	
	
	@Column(name = "STEP_EXECUTION_ID")
	private Long stepExecutionId;

}
