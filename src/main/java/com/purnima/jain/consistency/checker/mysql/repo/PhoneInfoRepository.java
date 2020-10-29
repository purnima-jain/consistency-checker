package com.purnima.jain.consistency.checker.mysql.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.purnima.jain.consistency.checker.mysql.model.MySqlPhoneInfo;
import com.purnima.jain.consistency.checker.util.Util;

@Repository
public class PhoneInfoRepository {

	private static final Logger logger = LoggerFactory.getLogger(PhoneInfoRepository.class);

	@Autowired
	@Qualifier("mySqlJdbcTemplate")
	public NamedParameterJdbcTemplate mySqlJdbcTemplate;

	public HashMap<String, List<MySqlPhoneInfo>> getPhonesInfoForCustomerIdList(List<String> customerIdList) {

		logger.debug("Entering PhoneInfoRepository.getPhonesInfoForCustomerIdList() with customerIdList:: {}", customerIdList);

		HashMap<String, List<MySqlPhoneInfo>> mySqlPhoneInfoListForCustomerIdMap = new HashMap<>();

		String phoneInfoSql = getPhonesInfoForCustomerIdListSql(customerIdList);
		logger.debug("Phone Info SQL:: {}", phoneInfoSql);

		List<MySqlPhoneInfo> mySqlPhoneInfoList = mySqlJdbcTemplate.query(phoneInfoSql, new MySqlPhoneInfoMapper());

		if (mySqlPhoneInfoList != null) {
			for (MySqlPhoneInfo mySqlPhoneInfo : mySqlPhoneInfoList) {
				if (mySqlPhoneInfoListForCustomerIdMap.get(mySqlPhoneInfo.getCustomerId()) == null) {
					mySqlPhoneInfoListForCustomerIdMap.put(mySqlPhoneInfo.getCustomerId(), new ArrayList<>());
				}
				mySqlPhoneInfoListForCustomerIdMap.get(mySqlPhoneInfo.getCustomerId()).add(mySqlPhoneInfo);
			}
		}

		return mySqlPhoneInfoListForCustomerIdMap;
	}

	private String getPhonesInfoForCustomerIdListSql(List<String> customerIdList) {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(" SELECT CUSTOMER_ID, PHONE_TYPE, PHONE_NUMBER, LAST_UPDATED ");
		stringBuilder.append(" FROM PHONE_INFO ");
		stringBuilder.append(" WHERE CUSTOMER_ID IN (");
		stringBuilder.append(Util.convertListToQuotedAndCommaDelimitedString(customerIdList));
		stringBuilder.append(")");
		stringBuilder.append(" ORDER BY CUSTOMER_ID DESC ");

		return stringBuilder.toString();
	}

}

class MySqlPhoneInfoMapper implements RowMapper<MySqlPhoneInfo> {

	@Override
	public MySqlPhoneInfo mapRow(ResultSet rs, int rowNum) throws SQLException {

		MySqlPhoneInfo mySqlPhoneInfo = new MySqlPhoneInfo();
		mySqlPhoneInfo.setCustomerId(rs.getString("CUSTOMER_ID"));
		mySqlPhoneInfo.setPhoneType(rs.getString("PHONE_TYPE"));
		mySqlPhoneInfo.setPhoneNumber(rs.getString("PHONE_NUMBER"));
		mySqlPhoneInfo.setLastUpdated(rs.getTimestamp("LAST_UPDATED").toLocalDateTime());

		return mySqlPhoneInfo;
	}

}
