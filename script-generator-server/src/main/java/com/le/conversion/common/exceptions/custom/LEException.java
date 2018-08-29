package com.le.conversion.common.exceptions.custom;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent=true)
public class LEException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter @Setter
	private String code;
	
	@Getter @Setter
	private String message;

	public LEException(String message) {
		// TODO Auto-generated constructor stub
		super(message);
	}
	
	public LEException(String message, String code) {
		// TODO Auto-generated constructor stub
		super();
		this.message = message;
		this.code = code;
	}
	
}
