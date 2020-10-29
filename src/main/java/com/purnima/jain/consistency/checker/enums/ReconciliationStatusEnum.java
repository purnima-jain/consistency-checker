package com.purnima.jain.consistency.checker.enums;

public enum ReconciliationStatusEnum {

	UNRECONCILED("UNRECONCILED", "Discrepancy is unreconciled");

	private final String key;
	private final String value;

	private ReconciliationStatusEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static ReconciliationStatusEnum getEnumByKey(String key) {
		for (ReconciliationStatusEnum reconciliationStatusEnum : ReconciliationStatusEnum.values()) {
			if (reconciliationStatusEnum.getKey().equals(key)) {
				return reconciliationStatusEnum;
			}
		}
		return null;
	}

}