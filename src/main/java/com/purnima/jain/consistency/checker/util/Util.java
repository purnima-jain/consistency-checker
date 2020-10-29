package com.purnima.jain.consistency.checker.util;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {

	// This function returns output that is suitable for IN-Clause in SQL
	public static String convertListToQuotedAndCommaDelimitedString(List<String> strList) {
		// Validation Check
		if (strList == null || strList.isEmpty())
			return "";

		// Input is fine
		return String.join(",", strList.stream().map(str -> ("'" + str + "'")).collect(Collectors.toList()));
	}

	public static String convertObjectToJson(Object object) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

		// Java Object to JSON String
		String jsonString = mapper.writeValueAsString(object);

		return jsonString;
	}

}
