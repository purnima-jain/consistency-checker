package com.purnima.jain.consistency.checker.mysql.service;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.purnima.jain.consistency.checker.mysql.model.MySqlCustomerInfo;
import com.purnima.jain.consistency.checker.mysql.model.MySqlEmailInfo;
import com.purnima.jain.consistency.checker.mysql.model.MySqlPhoneInfo;
import com.purnima.jain.consistency.checker.mysql.repo.CustomerInfoRepository;
import com.purnima.jain.consistency.checker.mysql.repo.EmailInfoRepository;
import com.purnima.jain.consistency.checker.mysql.repo.PhoneInfoRepository;

@Service
public class MySqlDataRetrievalService {
	
	private static final Logger logger = LoggerFactory.getLogger(MySqlDataRetrievalService.class);
	
	@Autowired
	private CustomerInfoRepository customerInfoRepository;
	
	@Autowired
	private PhoneInfoRepository phoneInfoRepository;
	
	@Autowired
	private EmailInfoRepository emailInfoRepository;
	
	// Getting CustomerInfo
	public HashMap<String, MySqlCustomerInfo> getCustomersInfoForCustomerIdList(List<String> customerIdList) {
		HashMap<String, MySqlCustomerInfo> mySqlCustomerInfoForCustomerIdMap = customerInfoRepository.getCustomersInfoForCustomerIdList(customerIdList);
		return mySqlCustomerInfoForCustomerIdMap;
	}
	
	// Getting Phone Info
	public HashMap<String, List<MySqlPhoneInfo>> getPhonesInfoForCustomerIdList(List<String> customerIdList) {
		HashMap<String, List<MySqlPhoneInfo>> mySqlPhoneInfoListForCustomerIdMap = phoneInfoRepository.getPhonesInfoForCustomerIdList(customerIdList);
		return mySqlPhoneInfoListForCustomerIdMap;
	}
	
	// Getting Email Info
	public HashMap<String, List<MySqlEmailInfo>> getEmailsInfoForCustomerIdList(List<String> customerIdList) {
		HashMap<String, List<MySqlEmailInfo>> mySqlEmailInfoListForCustomerIdMap = emailInfoRepository.getEmailsInfoForCustomerIdList(customerIdList);
		return mySqlEmailInfoListForCustomerIdMap;
	}

}
