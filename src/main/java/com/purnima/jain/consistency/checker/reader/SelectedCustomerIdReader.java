package com.purnima.jain.consistency.checker.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalDto;

@Component
public class SelectedCustomerIdReader {

	private static final Logger logger = LoggerFactory.getLogger(SelectedCustomerIdReader.class);

	@Value("${customer_id_batch_size}")
	private Integer customerIdBatchSize;

	@Autowired
	@Qualifier("postgresDataSource")
	private DataSource postgresDataSource;

	@Autowired
	private SecondaryReader secondaryReader;

	public CompositeJdbcPagingItemReader<ConsistencyCheckerInternalDto> getSelectedCustomerIdReader(Long jobInstanceId) {
		logger.debug("Entering SelectedCustomerIdReader.getSelectedCustomerIdReader() with jobInstanceId:: {}", jobInstanceId);

		CompositeJdbcPagingItemReader<ConsistencyCheckerInternalDto> reader = new CompositeJdbcPagingItemReader<>();
		reader.setDataSource(this.postgresDataSource);
		reader.setFetchSize(customerIdBatchSize);
		reader.setPageSize(customerIdBatchSize);
		reader.setQueryProvider(queryProvider(this.postgresDataSource, jobInstanceId));
		reader.setRowMapper(new ConsistencyCheckerInternalDtoRowMapper());
		
		reader.setPageReader(secondaryReader);

		return reader;
	}

	private PagingQueryProvider queryProvider(DataSource dataSource, Long jobInstanceId) {
		SqlPagingQueryProviderFactoryBean bean = new SqlPagingQueryProviderFactoryBean();
		bean.setDataSource(dataSource);
		bean.setSelectClause("SELECT customer_id ");
		bean.setFromClause(" FROM customer_consistency_checker_selection ");
		bean.setWhereClause(" WHERE job_instance_id = " + jobInstanceId);
		bean.setSortKey("customer_id");

		try {
			return bean.getObject();
		} catch (Exception e) {
			logger.error("Error!!!", e.getMessage());
			throw new RuntimeException(e);
		}
	}

}

class ConsistencyCheckerInternalDtoRowMapper implements RowMapper<ConsistencyCheckerInternalDto> {

	@Override
	public ConsistencyCheckerInternalDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		ConsistencyCheckerInternalDto consistencyCheckerInternalDto = new ConsistencyCheckerInternalDto();
		consistencyCheckerInternalDto.setCustomerId(rs.getString("CUSTOMER_ID"));
		return consistencyCheckerInternalDto;
	}
	
}

