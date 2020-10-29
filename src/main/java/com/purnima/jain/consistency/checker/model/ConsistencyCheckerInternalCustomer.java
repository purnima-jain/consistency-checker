package com.purnima.jain.consistency.checker.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsistencyCheckerInternalCustomer {

	private String customerId;
	private String firstName;
	private String lastName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime lastUpdated;

	private List<ConsistencyCheckerInternalPhone> phones = new ArrayList<>();

	private List<ConsistencyCheckerInternalEmail> emails = new ArrayList<>();

	public static Boolean isCustomerMainDetailsEqual(ConsistencyCheckerInternalCustomer internalCustomerMySql, ConsistencyCheckerInternalCustomer internalCustomerCassandra) {
		Boolean isCustomerMainDetailsEqual = Boolean.TRUE;

		if (!Objects.equals(internalCustomerMySql.getCustomerId(), internalCustomerCassandra.getCustomerId()))
			isCustomerMainDetailsEqual = Boolean.FALSE;
		else if (!Objects.equals(internalCustomerMySql.getFirstName(), internalCustomerCassandra.getFirstName()))
			isCustomerMainDetailsEqual = Boolean.FALSE;
		else if (!Objects.equals(internalCustomerMySql.getLastName(), internalCustomerCassandra.getLastName()))
			isCustomerMainDetailsEqual = Boolean.FALSE;
		else if (!Objects.equals(internalCustomerMySql.getLastUpdated(), internalCustomerCassandra.getLastUpdated()))
			isCustomerMainDetailsEqual = Boolean.FALSE;

		return isCustomerMainDetailsEqual;
	}

}
