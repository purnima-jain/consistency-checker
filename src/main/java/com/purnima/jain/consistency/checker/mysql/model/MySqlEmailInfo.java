package com.purnima.jain.consistency.checker.mysql.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MySqlEmailInfo {

	private String customerId;
	private String emailType;
	private String emailAddress;
	private LocalDateTime lastUpdated;

}
