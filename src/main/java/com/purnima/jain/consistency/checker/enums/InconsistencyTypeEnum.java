package com.purnima.jain.consistency.checker.enums;

public enum InconsistencyTypeEnum {

	CUSTOMER_MAIN_DETAILS("CUSTOMER_MAIN_DETAILS", "Customer Main Details"), 
	EMAILS("EMAILS", "Emails"), 
	PHONES("PHONES", "Phones");

	private final String key;
	private final String value;

	private InconsistencyTypeEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static InconsistencyTypeEnum getEnumByKey(String key) {
		for (InconsistencyTypeEnum inconsistencyTypeEnum : InconsistencyTypeEnum.values()) {
			if (inconsistencyTypeEnum.getKey().equals(key)) {
				return inconsistencyTypeEnum;
			}
		}
		return null;
	}

}
