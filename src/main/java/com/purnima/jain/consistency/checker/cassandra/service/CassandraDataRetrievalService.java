package com.purnima.jain.consistency.checker.cassandra.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.purnima.jain.consistency.checker.cassandra.entity.CustomerJsonEntity;
import com.purnima.jain.consistency.checker.cassandra.model.CassandraCustomer;
import com.purnima.jain.consistency.checker.cassandra.repo.CustomerJsonRepository;

@Service
public class CassandraDataRetrievalService {
	
	private static final Logger logger = LoggerFactory.getLogger(CassandraDataRetrievalService.class);
	
	@Autowired
	private CustomerJsonRepository customerJsonRepository;
	
	public HashMap<String, CassandraCustomer> getCustomersListForCustomerIdList(List<String> customerIdList) {
		logger.debug("Entering CassandraDataRetrievalService.getCustomersListForCustomerIdList() with customerIdList:: {}", customerIdList);
		HashMap<String, CassandraCustomer> cassandraCustomerListForCustomerIdMap = new HashMap<>();
		
		for(String customerId: customerIdList) {
			logger.debug("Retrieving data from Cassandra for customerId: {}", customerId);
			CustomerJsonEntity customerJsonEntity = customerJsonRepository.findByCustomerId(customerId);
			logger.debug("Retrieving data from Cassandra customerJsonEntity: {}", customerJsonEntity);
			CassandraCustomer cassandraCustomer = convertCustomerJsonToCassandraCustomer(customerJsonEntity.getCustomerJson());
			cassandraCustomerListForCustomerIdMap.put(customerId, cassandraCustomer);
		}
		
		logger.debug("Leaving CassandraDataRetrievalService.getCustomersListForCustomerIdList() with cassandraCustomerListForCustomerIdMap:: {}", cassandraCustomerListForCustomerIdMap);
		return cassandraCustomerListForCustomerIdMap;
	}
	
	private CassandraCustomer convertCustomerJsonToCassandraCustomer(String customerJson) {
		ObjectMapper mapper = new ObjectMapper();
		CassandraCustomer cassandraCustomer = null;
		try {
			cassandraCustomer = mapper.readValue(customerJson, CassandraCustomer.class);
		} catch (IOException e) {
			logger.error("Error parsing Json from Cassandra. Message:: {}", e.getMessage());
		}	
		return cassandraCustomer;
	}

}
