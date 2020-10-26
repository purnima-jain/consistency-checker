package com.purnima.jain.consistency.checker.reader;

import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.util.Assert;

public class CompositeJdbcPagingItemReader<T> extends JdbcPagingItemReader<T> {

	private PageReader<T> pageReader;

	public void setPageReader(PageReader<T> pageReader) {
		this.pageReader = pageReader;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(pageReader, "Page Reader cannot be null");
	}

	@Override
	protected void doReadPage() {
		super.doReadPage();
		if (!results.isEmpty()) {
			pageReader.read(results);
		}
	}

}
