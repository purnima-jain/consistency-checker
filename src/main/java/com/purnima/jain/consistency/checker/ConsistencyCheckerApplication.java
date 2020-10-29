package com.purnima.jain.consistency.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConsistencyCheckerApplication {

	private static final Logger logger = LoggerFactory.getLogger(ConsistencyCheckerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConsistencyCheckerApplication.class, args);
		logger.info("ConsistencyCheckerApplication Started........");
	}

}