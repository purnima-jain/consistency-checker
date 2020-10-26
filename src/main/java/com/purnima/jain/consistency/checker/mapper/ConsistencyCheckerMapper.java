package com.purnima.jain.consistency.checker.mapper;

import java.util.List;

import com.purnima.jain.consistency.checker.cassandra.model.CassandraCustomer;
import com.purnima.jain.consistency.checker.cassandra.model.CassandraEmail;
import com.purnima.jain.consistency.checker.cassandra.model.CassandraPhone;
import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalCustomer;
import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalEmail;
import com.purnima.jain.consistency.checker.model.ConsistencyCheckerInternalPhone;
import com.purnima.jain.consistency.checker.mysql.model.MySqlCustomerInfo;
import com.purnima.jain.consistency.checker.mysql.model.MySqlEmailInfo;
import com.purnima.jain.consistency.checker.mysql.model.MySqlPhoneInfo;

public class ConsistencyCheckerMapper {
	
	public static ConsistencyCheckerInternalCustomer mapCassandraCustomerToConsistencyCheckerInternalCustomer(CassandraCustomer cassandraCustomer) {
		ConsistencyCheckerInternalCustomer consistencyCheckerInternalCustomer = new ConsistencyCheckerInternalCustomer();
		
		consistencyCheckerInternalCustomer.setCustomerId(cassandraCustomer.getCustomerId());
		consistencyCheckerInternalCustomer.setFirstName(cassandraCustomer.getFirstName());
		consistencyCheckerInternalCustomer.setLastName(cassandraCustomer.getLastName());
		consistencyCheckerInternalCustomer.setLastUpdated(cassandraCustomer.getLastUpdated());
		
		for(CassandraPhone cassandraPhone : cassandraCustomer.getPhones()) {
			consistencyCheckerInternalCustomer.getPhones().add(new ConsistencyCheckerInternalPhone(cassandraPhone.getPhoneType(), cassandraPhone.getPhoneNumber(), cassandraPhone.getLastUpdated()));
		}
		
		for(CassandraEmail cassandraEmail : cassandraCustomer.getEmails()) {
			consistencyCheckerInternalCustomer.getEmails().add(new ConsistencyCheckerInternalEmail(cassandraEmail.getEmailType(), cassandraEmail.getEmailAddress(), cassandraEmail.getLastUpdated()));
		}
		
		return consistencyCheckerInternalCustomer;
	}
	
	public static ConsistencyCheckerInternalCustomer mapMySqlInfosToConsistencyCheckerInternalCustomer(MySqlCustomerInfo mySqlCustomerInfo, List<MySqlPhoneInfo> mySqlPhoneInfoList, List<MySqlEmailInfo> mySqlEmailInfoList) {
		ConsistencyCheckerInternalCustomer consistencyCheckerInternalCustomer = new ConsistencyCheckerInternalCustomer();
		
		consistencyCheckerInternalCustomer.setCustomerId(mySqlCustomerInfo.getCustomerId());
		consistencyCheckerInternalCustomer.setFirstName(mySqlCustomerInfo.getFirstName());
		consistencyCheckerInternalCustomer.setLastName(mySqlCustomerInfo.getLastName());
		consistencyCheckerInternalCustomer.setLastUpdated(mySqlCustomerInfo.getLastUpdated());
		
		for(MySqlPhoneInfo mySqlPhoneInfo : mySqlPhoneInfoList) {
			consistencyCheckerInternalCustomer.getPhones().add(new ConsistencyCheckerInternalPhone(mySqlPhoneInfo.getPhoneType(), mySqlPhoneInfo.getPhoneNumber(), mySqlPhoneInfo.getLastUpdated()));
		}
		
		for(MySqlEmailInfo mySqlEmailInfo : mySqlEmailInfoList) {
			consistencyCheckerInternalCustomer.getEmails().add(new ConsistencyCheckerInternalEmail(mySqlEmailInfo.getEmailType(), mySqlEmailInfo.getEmailAddress(), mySqlEmailInfo.getLastUpdated()));
		}
		
		return consistencyCheckerInternalCustomer;
	}

}
