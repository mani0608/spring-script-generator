package com.le.conversion.common;

public enum Messages {
	
	LOADER_START("Starting Loader"),
	MAPPING_PATH("Mapping file path"),
	LOADER_SUCCESS("Mapping successfully loaded"),
	LOADER_COMPLETE("Completed Loader"),
	LOADER_FAILED("Loading Mapping has failed"),
	PROCESSOR_START("Starting Processor"),
	PROCESSOR_SUCCESS("Mapping successfully processed"),
	PROCESSOR_FAILED("Processing Mapping has failed"),
	PROCESSOR_COMPLETE("Completed Processor"),
	FINISHER_START("Starting Finisher"),
	FINISHER_SUCCESS("Mapping successfully finished"),
	FINISHER_FAILED("Finisher has failed"),
	FINISHER_COMPLETE("Completed Finisher");
	
	private String message;
	
	Messages(String message) {
		this.message = message;
	}
	
	public String message() {
		return this.message;
	}

}
