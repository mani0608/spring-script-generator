package com.le.conversion.common;

public enum CacheConstants {
	
	GENERATOR_CACHE("generatorCache"),
	MAPPING_FILE_PATH("mappingFilePath"),
	MAPPINGS("mappings"),
	EXECUTION_DATA("executionData");
	
	
	private String value;
	
	CacheConstants(String value) {
		this.value = value;
	}
	
	public String val() {
		return this.value;
	}

}
