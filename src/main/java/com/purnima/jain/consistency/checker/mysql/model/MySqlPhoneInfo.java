package com.purnima.jain.consistency.checker.mysql.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MySqlPhoneInfo {
	
	private String customerId;
	private String phoneType;
	private String phoneNumber;
	private LocalDateTime lastUpdated;

}
