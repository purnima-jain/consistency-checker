package com.purnima.jain.consistency.checker.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConsistencyCheckerInternalPhone {
	
	private String phoneType;
	private String phoneNumber;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime lastUpdated;
	
	public static Boolean compareLists(List<ConsistencyCheckerInternalPhone> phonesMySqlList, List<ConsistencyCheckerInternalPhone> phonesCassandraList) {		
		Boolean isEquals = Boolean.TRUE;
		
		if(phonesMySqlList == null && phonesCassandraList != null) {
			isEquals = Boolean.FALSE;			
		} else if(phonesMySqlList != null && phonesCassandraList == null) {
			isEquals = Boolean.FALSE;
		} else if(phonesMySqlList.size() != phonesCassandraList.size()) {
			isEquals = Boolean.FALSE;			
		} else if(!(phonesMySqlList.containsAll(phonesCassandraList) && phonesCassandraList.containsAll(phonesMySqlList))) {
			isEquals = Boolean.FALSE;
		}
		
		return isEquals;		
	}

}
