package com.purnima.jain.consistency.checker.postgres.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "postgresEntityManagerFactory", 
		transactionManagerRef = "postgresTransactionManager", 
		basePackages = { "com.purnima.jain.consistency.checker.postgres.repo" })
public class PostgresConfig {

	@Primary
	@Bean(name = "postgresDataSource")
	@ConfigurationProperties(prefix = "spring.postgres.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean(name = "postgresEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("postgresDataSource") DataSource dataSource) {
		return builder.dataSource(dataSource).packages("com.purnima.jain.consistency.checker.postgres.entity").persistenceUnit("postgres").build();
	}

	@Primary
	@Bean(name = "postgresTransactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("postgresEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	// Configuring Postgres JDBCTemplate
	@Bean(name = "postgresJdbcTemplate")
	public NamedParameterJdbcTemplate postgresJdbcTemplate(@Qualifier("postgresDataSource") DataSource postgresDataSource) {
		return new NamedParameterJdbcTemplate(postgresDataSource);
	}

}