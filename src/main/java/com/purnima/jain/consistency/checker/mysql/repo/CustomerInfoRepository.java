package com.purnima.jain.consistency.checker.mysql.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.purnima.jain.consistency.checker.mysql.model.MySqlCustomerInfo;
import com.purnima.jain.consistency.checker.util.Util;

@Repository
public class CustomerInfoRepository {

	private static final Logger logger = LoggerFactory.getLogger(CustomerInfoRepository.class);

	@Autowired
	@Qualifier("mySqlJdbcTemplate")
	public NamedParameterJdbcTemplate mySqlJdbcTemplate;

	public HashMap<String, MySqlCustomerInfo> getCustomersInfoForCustomerIdList(List<String> customerIdList) {

		logger.debug("Entering CustomerInfoRepository.getCustomersInfoForCustomerIdList() with customerIdList:: {}", customerIdList);

		HashMap<String, MySqlCustomerInfo> mySqlCustomerInfoForCustomerIdMap = new HashMap<>();

		String customerInfoSql = getCustomersInfoForCustomerIdListSql(customerIdList);
		logger.debug("Customer Info SQL:: {}", customerInfoSql);

		List<MySqlCustomerInfo> mySqlCustomerInfoList = mySqlJdbcTemplate.query(customerInfoSql, new MySqlCustomerInfoMapper());

		if (mySqlCustomerInfoList != null) {
			for (MySqlCustomerInfo mySqlCustomerInfo : mySqlCustomerInfoList) {
				mySqlCustomerInfoForCustomerIdMap.put(mySqlCustomerInfo.getCustomerId(), mySqlCustomerInfo);
			}
		}

		return mySqlCustomerInfoForCustomerIdMap;
	}

	private String getCustomersInfoForCustomerIdListSql(List<String> customerIdList) {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(" SELECT CUSTOMER_ID, FIRST_NAME, LAST_NAME, LAST_UPDATED ");
		stringBuilder.append(" FROM CUSTOMER_INFO ");
		stringBuilder.append(" WHERE CUSTOMER_ID IN (");
		stringBuilder.append(Util.convertListToQuotedAndCommaDelimitedString(customerIdList));
		stringBuilder.append(")");
		stringBuilder.append(" ORDER BY CUSTOMER_ID DESC ");

		return stringBuilder.toString();
	}

}

class MySqlCustomerInfoMapper implements RowMapper<MySqlCustomerInfo> {

	@Override
	public MySqlCustomerInfo mapRow(ResultSet rs, int rowNum) throws SQLException {

		MySqlCustomerInfo mySqlCustomerInfo = new MySqlCustomerInfo();
		mySqlCustomerInfo.setCustomerId(rs.getString("CUSTOMER_ID"));
		mySqlCustomerInfo.setFirstName(rs.getString("FIRST_NAME"));
		mySqlCustomerInfo.setLastName(rs.getString("LAST_NAME"));
		mySqlCustomerInfo.setLastUpdated(rs.getTimestamp("LAST_UPDATED").toLocalDateTime());

		return mySqlCustomerInfo;
	}

}
