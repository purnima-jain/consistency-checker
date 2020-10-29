package com.purnima.jain.consistency.checker.cassandra.entity;

import javax.persistence.Entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "customer_json")
public class CustomerJsonEntity {

	@PrimaryKey("customer_id")
	private String customerId;

	@Column("customer_json")
	private String customerJson;

}
