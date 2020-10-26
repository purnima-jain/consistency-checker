package com.purnima.jain.consistency.checker.cassandra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = { "com.purnima.jain.consistency.checker.cassandra.repo" })
public class CassandraDbConfig extends AbstractCassandraConfiguration {

	@Value("${spring.data.cassandra.contact-points}")
	private String contactPoints;

	@Value("${spring.data.cassandra.port}")
	private String port;

	@Value("${spring.data.cassandra.cluster-name}")
	private String clusterName;

	@Value("${spring.data.cassandra.username}")
	private String username;

	@Value("${spring.data.cassandra.password}")
	private String password;

	@Value("${spring.data.cassandra.keyspace-name}")
	private String keyspaceName;

	@Bean
	public CassandraClusterFactoryBean cluster() {
		CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
		cluster.setContactPoints(contactPoints);
		cluster.setPort(Integer.parseInt(port));
		cluster.setClusterName(clusterName);
		cluster.setUsername(username);
		cluster.setPassword(password);
		cluster.setMetricsEnabled(Boolean.FALSE);

		return cluster;
	}

	@Override
	protected String getKeyspaceName() {
		return keyspaceName;
	}

}