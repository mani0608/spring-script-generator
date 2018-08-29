package com.le.conversion.common.exceptions;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import lombok.extern.log4j.Log4j2;

/**
 * Reference: https://stackoverflow.com/questions/25643348/java-8-method-reference-unhandled-exception
 * @author Manikandan.R
 *
 */


@Log4j2
public class ExceptionWrapper {

	public static <T, E extends Exception> Consumer<T> handleConsumer(
			ExeceptionConsumer<T, E> loaderConsumer) {

		return t -> {

			try {
				loaderConsumer.accept(t);
			} catch (Exception e) {
				log.debug("Exception occured: ", e);
			}
		};

	}
	
	public static <T, E extends Exception> Supplier<T> handleSupplier(
				ExceptionSupplier<T, E> loaderSupplier, Class<T> clazz) {

		return () -> {

			try {
				return clazz.cast(loaderSupplier.get());
			} catch (Exception e) {
				log.debug("Exception occured: ", e);
				return clazz.cast(e.getMessage());
			}
			
		};

	}
	
	
	public static <T, E extends Exception> Predicate<T> handlePredicate(
			ExceptionPredicate<T, E> loaderPredicate) {

	return t -> {

		try {
			return loaderPredicate.test(t);
		} catch (Exception e) {
			log.debug("Exception occured: ", e);
			//return clazz.cast(e.getMessage());
			return false;
		}
		
	};

}

}