package com.purnima.jain.consistency.checker.enums;

public enum ProcessingStatusEnum {

	PROCESSED("PROCESSED", "Customer Id is processed"), 
	UNPROCESSED("UNPROCESSED", "Customer Id is NOT processed");

	private final String key;
	private final String value;

	private ProcessingStatusEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static ProcessingStatusEnum getEnumByKey(String key) {
		for (ProcessingStatusEnum processingStatusEnum : ProcessingStatusEnum.values()) {
			if (processingStatusEnum.getKey().equals(key)) {
				return processingStatusEnum;
			}
		}
		return null;
	}

}
