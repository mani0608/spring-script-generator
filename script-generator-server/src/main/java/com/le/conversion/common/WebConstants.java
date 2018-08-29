package com.le.conversion.common;

public enum WebConstants {
	
	FORWARD_ANG_PATH("forward:/index.html");
	
	private String value;
	
	WebConstants(String value) {
		this.value = value;
	}
	
	public String val() {
		return this.value;
	}

}
