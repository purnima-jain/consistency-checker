package com.purnima.jain.consistency.checker.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ConsistencyCheckerInternalDto {
	
	private String customerId;
	
	private ConsistencyCheckerInternalCustomer mySqlCustomer;
	
	private ConsistencyCheckerInternalCustomer cassandraCustomer;
	
	private List<ConsistencyCheckerInternalDiscrepancyDto> discrepancyList = new ArrayList<>();
	
	

}
