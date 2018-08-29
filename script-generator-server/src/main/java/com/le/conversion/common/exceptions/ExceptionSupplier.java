package com.le.conversion.common.exceptions;

@FunctionalInterface
public interface ExceptionSupplier <T, E extends Exception> {
    T get() throws E;
}
