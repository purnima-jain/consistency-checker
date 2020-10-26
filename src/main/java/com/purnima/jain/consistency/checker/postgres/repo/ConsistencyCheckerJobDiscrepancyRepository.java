package com.purnima.jain.consistency.checker.postgres.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.purnima.jain.consistency.checker.postgres.entity.CustomerConsistencyCheckerJobDiscrepancyEntity;

@Repository
public class ConsistencyCheckerJobDiscrepancyRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(ConsistencyCheckerJobDiscrepancyRepository.class);
	
	@Autowired
	@Qualifier("postgresJdbcTemplate")
	private NamedParameterJdbcTemplate postgresJdbcTemplate;
	
	public void save(CustomerConsistencyCheckerJobDiscrepancyEntity customerConsistencyCheckerJobDiscrepancyEntity) {
		
		String sqlForInsert = getSqlForInsert();
		
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("id", customerConsistencyCheckerJobDiscrepancyEntity.getId());
		mapSqlParameterSource.addValue("customerId", customerConsistencyCheckerJobDiscrepancyEntity.getCustomerId());
		mapSqlParameterSource.addValue("inconsistencyType", customerConsistencyCheckerJobDiscrepancyEntity.getInconsistencyType());
		mapSqlParameterSource.addValue("mySqlContents", customerConsistencyCheckerJobDiscrepancyEntity.getMySqlContents());
		mapSqlParameterSource.addValue("cassandraContents", customerConsistencyCheckerJobDiscrepancyEntity.getCassandraContents());
		mapSqlParameterSource.addValue("reconciliationStatus", customerConsistencyCheckerJobDiscrepancyEntity.getReconciliationStatus());
		
		mapSqlParameterSource.addValue("jobInstanceId", customerConsistencyCheckerJobDiscrepancyEntity.getJobInstanceId());
		mapSqlParameterSource.addValue("jobExecutionId", customerConsistencyCheckerJobDiscrepancyEntity.getJobExecutionId());		
		mapSqlParameterSource.addValue("stepExecutionId", customerConsistencyCheckerJobDiscrepancyEntity.getStepExecutionId());
		
		postgresJdbcTemplate.update(sqlForInsert, mapSqlParameterSource);
	}
	
	private String getSqlForInsert() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(" INSERT INTO customer_consistency_checker_discrepancy");
		stringBuilder.append(" (ID, CUSTOMER_ID, INCONSISTENCY_TYPE, MYSQL_CONTENTS, CASSANDRA_CONTENTS, RECONCILIATION_STATUS, JOB_INSTANCE_ID, JOB_EXECUTION_ID, STEP_EXECUTION_ID) ");
		stringBuilder.append( "VALUES(:id, :customerId, :inconsistencyType, :mySqlContents, :cassandraContents, :reconciliationStatus, :jobInstanceId, :jobExecutionId, :stepExecutionId)");
		
		return stringBuilder.toString();		
	}
	

}
