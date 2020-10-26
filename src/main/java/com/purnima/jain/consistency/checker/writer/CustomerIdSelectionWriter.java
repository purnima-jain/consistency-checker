package com.purnima.jain.consistency.checker.writer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.purnima.jain.consistency.checker.postgres.entity.CustomerConsistencyCheckerJobSelectionEntity;
import com.purnima.jain.consistency.checker.postgres.repo.CustomerConsistencyCheckerJobSelectionRepository;

@Component
public class CustomerIdSelectionWriter implements ItemWriter<CustomerConsistencyCheckerJobSelectionEntity> {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomerIdSelectionWriter.class);
	
	@Autowired
	private CustomerConsistencyCheckerJobSelectionRepository customerConsistencyCheckerJobSelectionRepository;
	
	@Override
	public void write(List<? extends CustomerConsistencyCheckerJobSelectionEntity> customerConsistencyCheckerJobSelectionEntityList) throws Exception {
		logger.debug("Entering CustomerIdSelectionWriter.write() with customerConsistencyCheckerJobSelectionEntityList.size():: {}", customerConsistencyCheckerJobSelectionEntityList.size());
		
		for(CustomerConsistencyCheckerJobSelectionEntity customerConsistencyCheckerJobSelectionEntity : customerConsistencyCheckerJobSelectionEntityList) {
			customerConsistencyCheckerJobSelectionRepository.save(customerConsistencyCheckerJobSelectionEntity);
		}
		
		logger.debug("Leaving CustomerIdSelectionWriter.write()............");
	}

}
