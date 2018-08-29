package com.le.conversion.common.exceptions;

@FunctionalInterface
public interface ExceptionPredicate <T, E extends Exception> {

	Boolean test (T t) throws E;
	
}
