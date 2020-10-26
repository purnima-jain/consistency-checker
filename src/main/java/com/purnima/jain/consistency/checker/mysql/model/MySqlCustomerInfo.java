package com.purnima.jain.consistency.checker.mysql.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MySqlCustomerInfo {
	
	private String customerId;
	private String firstName;
	private String lastName;
	private LocalDateTime lastUpdated;
}
