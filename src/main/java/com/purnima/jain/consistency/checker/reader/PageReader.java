package com.purnima.jain.consistency.checker.reader;

import java.util.List;

public interface PageReader<T> {
	void read(List<T> page);
}
