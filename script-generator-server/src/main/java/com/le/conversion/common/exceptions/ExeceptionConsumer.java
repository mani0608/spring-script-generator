package com.le.conversion.common.exceptions;

@FunctionalInterface
public interface ExeceptionConsumer<T, E extends Exception> {

	void accept(T t) throws E;
	
}
