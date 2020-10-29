package com.purnima.jain.consistency.checker.postgres.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.purnima.jain.consistency.checker.enums.ProcessingStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_consistency_checker_selection")
public class CustomerConsistencyCheckerJobSelectionEntity {

	@SuppressWarnings("unused")
	private static final Long serialVersionUid = 1L;

	@Id
	@Column(name = "SELECTION_ID")
	private Integer selectionId;

	@Column(name = "JOB_INSTANCE_ID")
	protected Long jobInstanceId;

	@Column(name = "JOB_EXECUTION_ID")
	protected Long jobExecutionId;

	@Column(name = "STEP_EXECUTION_ID")
	protected Long stepExecutionId;

	@Column(name = "CUSTOMER_ID")
	protected String customerId;

	@Enumerated(EnumType.STRING)
	@Column(name = "PROCESSING_STATUS")
	protected ProcessingStatusEnum processingStatusEnum;

}
