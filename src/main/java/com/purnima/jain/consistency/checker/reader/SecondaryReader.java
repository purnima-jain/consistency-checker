package com.purnima.jain.consistency.checker.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.purnima.jain.consistency.checker.cassandra.model.CassandraCustomer;
import com.purnima.jain.consistency.checker.cassandra.service.CassandraDataRetrievalService;
import com.purnima.jain.consistency.checker.mapper.ConsistencyCheckerMapper;
import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalDto;
import com.purnima.jain.consistency.checker.mysql.model.MySqlCustomerInfo;
import com.purnima.jain.consistency.checker.mysql.model.MySqlEmailInfo;
import com.purnima.jain.consistency.checker.mysql.model.MySqlPhoneInfo;
import com.purnima.jain.consistency.checker.mysql.service.MySqlDataRetrievalService;

@Component
public class SecondaryReader implements PageReader<ConsistencyCheckerInternalDto> {
	private static final Logger logger = LoggerFactory.getLogger(SecondaryReader.class);
	
	@Value("${data.retrieval.timeout.in.milliseconds:5000}")
	private Integer dataRetrievalTimeoutInMilliseconds;
	
	@Autowired
	private MySqlDataRetrievalService mySqlDataRetrievalService;
	
	@Autowired
	private CassandraDataRetrievalService cassandraDataRetrievalService;
	
	private final Executor executor = Executors.newFixedThreadPool(7, new ThreadFactory() {
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		}
	});
	
	@Override
	public void read(List<ConsistencyCheckerInternalDto> consistencyCheckerInternalDtoList) {
		logger.debug("Entering SecondaryReader.read() with consistencyCheckerInternalDtoList:: {}", consistencyCheckerInternalDtoList);
		
		List<String> customerIdList = getCustomerIdList(consistencyCheckerInternalDtoList);
		
		// Customers from MySql
		Future<HashMap<String, MySqlCustomerInfo>> mySqlCustomerInfoCompletableFuture = getMySqlCustomersInfoForCustomerIdListASync(customerIdList);
		
		// Phones from MySql
		Future<HashMap<String, List<MySqlPhoneInfo>>> mySqlPhoneInfoListCompletableFuture = getMySqlPhonesInfoForCustomerIdListASync(customerIdList);
		
		// Emails from MySql
		Future<HashMap<String, List<MySqlEmailInfo>>> mySqlEmailInfoListCompletableFuture = getMySqlEmailsInfoForCustomerIdListASync(customerIdList);
		
		// Customer Data from Cassandra
		Future<HashMap<String, CassandraCustomer>> cassandraCustomerListCompletableFuture = getCassandraCustomersForCustomerIdListASync(customerIdList);
		
		try {
			// Customers from MySql
			HashMap<String, MySqlCustomerInfo> mySqlCustomerInfoForCustomerIdMap = mySqlCustomerInfoCompletableFuture.get(dataRetrievalTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
			
			// Phones from MySql
			HashMap<String, List<MySqlPhoneInfo>> mySqlPhoneInfoListForCustomerIdMap = mySqlPhoneInfoListCompletableFuture.get(dataRetrievalTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
			
			// Emails from MySql
			HashMap<String, List<MySqlEmailInfo>> mySqlEmailInfoListForCustomerIdMap = mySqlEmailInfoListCompletableFuture.get(dataRetrievalTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
			
			// Customer Data from Cassandra
			HashMap<String, CassandraCustomer> cassandraCustomerListForCustomerIdMap = cassandraCustomerListCompletableFuture.get(dataRetrievalTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
			
			// Aggregating all data retrieved from DB2 and Cassandra
			aggregateConsistencyCheckerData(consistencyCheckerInternalDtoList, mySqlCustomerInfoForCustomerIdMap, mySqlPhoneInfoListForCustomerIdMap, mySqlEmailInfoListForCustomerIdMap, cassandraCustomerListForCustomerIdMap);
		
		} catch(InterruptedException | ExecutionException | TimeoutException exception) {
			logger.error("Exception thrown while retrieving data. Message:: {}", exception.getMessage());
			throw new RuntimeException("Exception thrown while retrieving data. Message:: " + exception.getMessage(), exception);
		}
		logger.debug("Leaving SecondaryReader.read()..............");
		
	}
	
	// Customers from MySql
	Future<HashMap<String, MySqlCustomerInfo>> getMySqlCustomersInfoForCustomerIdListASync(List<String> customerIdList) {
		return CompletableFuture.supplyAsync(() -> {
			return mySqlDataRetrievalService.getCustomersInfoForCustomerIdList(customerIdList);			
		}, executor);		
	}
	
	// Phones from MySql
	Future<HashMap<String, List<MySqlPhoneInfo>>> getMySqlPhonesInfoForCustomerIdListASync(List<String> customerIdList) {
		return CompletableFuture.supplyAsync(() -> {
			return mySqlDataRetrievalService.getPhonesInfoForCustomerIdList(customerIdList);			
		}, executor);		
	}
	
	// Emails from MySql
	Future<HashMap<String, List<MySqlEmailInfo>>> getMySqlEmailsInfoForCustomerIdListASync(List<String> customerIdList) {
		return CompletableFuture.supplyAsync(() -> {
			return mySqlDataRetrievalService.getEmailsInfoForCustomerIdList(customerIdList);			
		}, executor);		
	}
	
	// Customers from Cassandra
	Future<HashMap<String, CassandraCustomer>> getCassandraCustomersForCustomerIdListASync(List<String> customerIdList) {
		return CompletableFuture.supplyAsync(() -> {
			return cassandraDataRetrievalService.getCustomersListForCustomerIdList(customerIdList);			
		}, executor);		
	}
	
	private void aggregateConsistencyCheckerData(List<ConsistencyCheckerInternalDto> consistencyCheckerInternalDtoList,
			HashMap<String, MySqlCustomerInfo> mySqlCustomerInfoForCustomerIdMap, HashMap<String, List<MySqlPhoneInfo>> mySqlPhoneInfoListForCustomerIdMap,
			HashMap<String, List<MySqlEmailInfo>> mySqlEmailInfoListForCustomerIdMap, HashMap<String, CassandraCustomer> cassandraCustomerListForCustomerIdMap) {

		for (ConsistencyCheckerInternalDto consistencyCheckerInternalDto : consistencyCheckerInternalDtoList) {
			String customerId = consistencyCheckerInternalDto.getCustomerId();

			// MySql
			MySqlCustomerInfo mySqlCustomerInfo = mySqlCustomerInfoForCustomerIdMap.get(customerId);
			List<MySqlPhoneInfo> mySqlPhoneInfoList = mySqlPhoneInfoListForCustomerIdMap.get(customerId);
			List<MySqlEmailInfo> mySqlEmailInfoList = mySqlEmailInfoListForCustomerIdMap.get(customerId);
			consistencyCheckerInternalDto.setMySqlCustomer(ConsistencyCheckerMapper.mapMySqlInfosToConsistencyCheckerInternalCustomer(mySqlCustomerInfo, mySqlPhoneInfoList, mySqlEmailInfoList));

			// Cassandra
			CassandraCustomer cassandraCustomer = cassandraCustomerListForCustomerIdMap.get(customerId);
			consistencyCheckerInternalDto.setCassandraCustomer(ConsistencyCheckerMapper.mapCassandraCustomerToConsistencyCheckerInternalCustomer(cassandraCustomer));
		}

	}
	
	private List<String> getCustomerIdList(List<ConsistencyCheckerInternalDto> consistencyCheckerInternalDtoList) {
		List<String> customerIdList = new ArrayList<>();

		if (consistencyCheckerInternalDtoList == null || consistencyCheckerInternalDtoList.isEmpty())
			return customerIdList;

		customerIdList.addAll(consistencyCheckerInternalDtoList.stream()
				.map(consistencyCheckerInternalDto -> consistencyCheckerInternalDto.getCustomerId())
				.collect(Collectors.toList()));

		return customerIdList;
	}
}
