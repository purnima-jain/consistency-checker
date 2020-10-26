package com.purnima.jain.consistency.checker.postgres.repo;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.purnima.jain.consistency.checker.util.Constants;

@Repository
public class SpringBatchRepository {

	private static final Logger logger = LoggerFactory.getLogger(SpringBatchRepository.class);

	@Autowired
	@Qualifier("postgresJdbcTemplate")
	public NamedParameterJdbcTemplate postgresJdbcTemplate;

	private static final String SQL = "" 
			+ " SELECT max(P3.long_val) "
			+ " FROM BATCH_JOB_EXECUTION_PARAMS P1, BATCH_JOB_EXECUTION_PARAMS P2, BATCH_JOB_EXECUTION_PARAMS P3 "
			+ " WHERE "
			+ " P1.key_name = 'from' "
			+ " AND P1.string_val = :from "
			+ " AND P2.key_name = 'to' "
			+ " AND P2.string_val = :to "
			+ " AND P1.job_execution_id = P2.job_execution_id "
			+ " AND P3.key_name = '" + Constants.RUN_COUNTER_ATTRIBUTE_NAME + "' "
			+ " AND P2.job_execution_id = P3.job_execution_id ";

	public Long getMaxRunCounterForGivenParameters(JobParameters jobParameters) {
		logger.debug("Entering SpringBatchRepository.getMaxRunCounterForGivenParameters() with parameters:: {}", jobParameters);

		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("from", jobParameters.getString("from"));
		namedParameters.put("to", jobParameters.getString("to"));

		Long maxRunCounterForGivenParameters = postgresJdbcTemplate.queryForObject(SQL, namedParameters, Long.class);
		return maxRunCounterForGivenParameters;
	}

}
