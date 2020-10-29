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
public class ConsistencyCheckerInternalEmail {

	private String emailType;
	private String emailAddress;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime lastUpdated;

	public static Boolean compareLists(List<ConsistencyCheckerInternalEmail> emailsMySqlList, List<ConsistencyCheckerInternalEmail> emailsCassandraList) {
		Boolean isEquals = Boolean.TRUE;

		if (emailsMySqlList == null && emailsCassandraList != null) {
			isEquals = Boolean.FALSE;
		} else if (emailsMySqlList != null && emailsCassandraList == null) {
			isEquals = Boolean.FALSE;
		} else if (emailsMySqlList.size() != emailsCassandraList.size()) {
			isEquals = Boolean.FALSE;
		} else if (!(emailsMySqlList.containsAll(emailsCassandraList) && emailsCassandraList.containsAll(emailsMySqlList))) {
			isEquals = Boolean.FALSE;
		}

		return isEquals;
	}

}
