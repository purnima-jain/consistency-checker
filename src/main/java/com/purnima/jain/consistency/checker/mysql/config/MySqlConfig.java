package com.purnima.jain.consistency.checker.mysql.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class MySqlConfig {

	@Bean(name = "mySqlDataSource")
	@ConfigurationProperties(prefix = "spring.mysql.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "mySqlJdbcTemplate")
	public NamedParameterJdbcTemplate mySqlJdbcTemplate(@Qualifier("mySqlDataSource") DataSource mySqlDataSource) {
		return new NamedParameterJdbcTemplate(mySqlDataSource);
	}

}