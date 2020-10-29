package com.purnima.jain.consistency.checker.postgres.repo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.purnima.jain.consistency.checker.enums.ProcessingStatusEnum;
import com.purnima.jain.consistency.checker.postgres.entity.CustomerConsistencyCheckerJobSelectionEntity;

@Repository
public class CustomerConsistencyCheckerJobSelectionRepository {

	private static final Logger logger = LoggerFactory.getLogger(CustomerConsistencyCheckerJobSelectionRepository.class);

	private static final String INSERT_SQL = "INSERT INTO customer_consistency_checker_selection(selection_id, job_instance_id, job_execution_id, step_execution_id, customer_id, processing_status) "
			+ " VALUES(CAST(:selectionId AS UUID), CAST(:jobInstanceId AS INT), CAST(:jobExecutionId AS INT), CAST(:stepExecutionId AS INT), :customerId, :processingStatus) "; // cast(:parameters AS JSON)
			
	private static final String UPDATE_STATUS_SQL = "UPDATE customer_consistency_checker_selection SET processing_status = :processingStatus WHERE job_instance_id = CAST(:jobInstanceId AS INT) AND customer_id = :customerId ";
	
	@Autowired
	@Qualifier("postgresJdbcTemplate")
	private NamedParameterJdbcTemplate postgresJdbcTemplate;

	public void save(CustomerConsistencyCheckerJobSelectionEntity customerConsistencyCheckerJobSelectionEntity) {
		logger.debug("Entering CustomerConsistencyCheckerJobSelectionRepository.save().......");

		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("selectionId", "" + UUID.randomUUID());
		namedParameters.put("jobInstanceId", "" + customerConsistencyCheckerJobSelectionEntity.getJobInstanceId());
		namedParameters.put("jobExecutionId", "" + customerConsistencyCheckerJobSelectionEntity.getJobExecutionId());
		namedParameters.put("stepExecutionId", "" + customerConsistencyCheckerJobSelectionEntity.getStepExecutionId());
		namedParameters.put("customerId", customerConsistencyCheckerJobSelectionEntity.getCustomerId());
		namedParameters.put("processingStatus", customerConsistencyCheckerJobSelectionEntity.getProcessingStatusEnum().getKey());

		postgresJdbcTemplate.update(INSERT_SQL, namedParameters);

		logger.debug("Leaving CustomerConsistencyCheckerJobSelectionRepository.save().......");
	}

	public void updateProcessingStatus(Long jobInstanceId, String customerId, ProcessingStatusEnum processingStatusEnum) {
		logger.debug("Entering CustomerConsistencyCheckerJobSelectionRepository.updateProcessingStatus() with customerId: {} and processingStatusEnum: {}", customerId,
				processingStatusEnum);

		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("jobInstanceId", "" + jobInstanceId);
		namedParameters.put("customerId", customerId);
		namedParameters.put("processingStatus", processingStatusEnum.getKey());

		postgresJdbcTemplate.update(UPDATE_STATUS_SQL, namedParameters);

		logger.debug("Leaving CustomerConsistencyCheckerJobSelectionRepository.updateProcessingStatus().......");
	}

}
