package com.purnima.jain.consistency.checker.reader;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CustomerIdSelectionReader {

	private static final Logger logger = LoggerFactory.getLogger(CustomerIdSelectionReader.class);

	// private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // MySQL DateTime Format is YYYY-MM-DD HH:MM:SS

	@Autowired
	@Qualifier("mySqlDataSource")
	private DataSource mySqlDataSource;

	public JdbcCursorItemReader<String> getCustomerIdSelectionReader(String from, String to) {
		logger.debug("Entering CustomerIdSelectionReader.getCustomerIdSelectionReader()......................");
		String sqlQuery = getInvokeForPeriodSql(from, to);
		logger.debug("sqlQuery:: {}", sqlQuery);

		return new JdbcCursorItemReaderBuilder<String>()
				.name("customerIdSelectionReader")
				.sql(sqlQuery)
				.rowMapper((rs, i) -> rs.getString("CUSTOMER_ID"))
				.dataSource(mySqlDataSource)
				.build();
	}

	private String getInvokeForPeriodSql(String from, String to) {
		StringBuilder whereClauseBuilder = new StringBuilder(10);
		whereClauseBuilder.append(" WHERE LAST_UPDATED BETWEEN ");
		whereClauseBuilder.append(" '" + from + "' ");
		whereClauseBuilder.append(" AND '" + to + "' ");
		String whereClause = whereClauseBuilder.toString();

		StringBuilder stringBuilder = new StringBuilder(20);
		stringBuilder.append(" SELECT CUSTOMER_ID FROM CUSTOMER_INFO ");
		stringBuilder.append(whereClause);
		stringBuilder.append(" UNION ");

		stringBuilder.append(" SELECT CUSTOMER_ID FROM PHONE_INFO ");
		stringBuilder.append(whereClause);
		stringBuilder.append(" UNION ");

		stringBuilder.append(" SELECT CUSTOMER_ID FROM EMAIL_INFO ");
		stringBuilder.append(whereClause);

		return stringBuilder.toString();
	}

}
