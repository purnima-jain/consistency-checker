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

import com.purnima.jain.consistency.checker.mysql.model.MySqlEmailInfo;
import com.purnima.jain.consistency.checker.util.Util;

@Repository
public class EmailInfoRepository {

private static final Logger logger = LoggerFactory.getLogger(EmailInfoRepository.class);
	
	@Autowired
	@Qualifier("mySqlJdbcTemplate")
	public NamedParameterJdbcTemplate mySqlJdbcTemplate;
	
	public HashMap<String, List<MySqlEmailInfo>> getEmailsInfoForCustomerIdList(List<String> customerIdList) {
		
		logger.debug("Entering EmailInfoRepository.getEmailsInfoForCustomerIdList() with customerIdList:: {}", customerIdList);
		
		HashMap<String, List<MySqlEmailInfo>> mySqlEmailInfoListForCustomerIdMap = new HashMap<>();
		
		String emailInfoSql = getEmailsInfoForCustomerIdListSql(customerIdList);
		logger.debug("Email Info SQL:: {}", emailInfoSql);
		
		List<MySqlEmailInfo> mySqlEmailInfoList = mySqlJdbcTemplate.query(emailInfoSql, new MySqlEmailInfoMapper());
		
		if(mySqlEmailInfoList != null) {
			for(MySqlEmailInfo mySqlEmailInfo : mySqlEmailInfoList) {
				if(mySqlEmailInfoListForCustomerIdMap.get(mySqlEmailInfo.getCustomerId()) == null) {
					mySqlEmailInfoListForCustomerIdMap.put(mySqlEmailInfo.getCustomerId(), new ArrayList<>());					
				}
				mySqlEmailInfoListForCustomerIdMap.get(mySqlEmailInfo.getCustomerId()).add(mySqlEmailInfo);
			}
		}		
		
		return mySqlEmailInfoListForCustomerIdMap;
	}
	
	private String getEmailsInfoForCustomerIdListSql(List<String> customerIdList) {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(" SELECT CUSTOMER_ID, EMAIL_TYPE, EMAIL_ADDRESS, LAST_UPDATED ");
		stringBuilder.append(" FROM EMAIL_INFO ");
		stringBuilder.append(" WHERE CUSTOMER_ID IN (");
		stringBuilder.append(Util.convertListToQuotedAndCommaDelimitedString(customerIdList));
		stringBuilder.append(")");
		stringBuilder.append(" ORDER BY CUSTOMER_ID DESC ");
		
		return stringBuilder.toString();		
	}

}

class MySqlEmailInfoMapper implements RowMapper<MySqlEmailInfo> {

	@Override
	public MySqlEmailInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		MySqlEmailInfo mySqlEmailInfo = new MySqlEmailInfo();
		mySqlEmailInfo.setCustomerId(rs.getString("CUSTOMER_ID"));
		mySqlEmailInfo.setEmailType(rs.getString("EMAIL_TYPE"));
		mySqlEmailInfo.setEmailAddress(rs.getString("EMAIL_ADDRESS"));
		mySqlEmailInfo.setLastUpdated(rs.getTimestamp("LAST_UPDATED").toLocalDateTime());
		
		return mySqlEmailInfo;
	}
	
}
