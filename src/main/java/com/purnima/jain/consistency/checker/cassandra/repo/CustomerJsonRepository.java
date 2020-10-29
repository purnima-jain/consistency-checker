package com.purnima.jain.consistency.checker.cassandra.repo;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.purnima.jain.consistency.checker.cassandra.entity.CustomerJsonEntity;


@Repository
public interface CustomerJsonRepository extends CassandraRepository<CustomerJsonEntity, String> {
	
}
