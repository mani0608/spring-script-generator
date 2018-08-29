package com.le.conversion.common.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.le.conversion.common.Constants;
import com.le.conversion.common.exceptions.ExceptionWrapper;
import com.le.conversion.common.exceptions.custom.CommonException;
import com.le.conversion.common.functions.MultiplePredicates;
import com.le.conversion.model.Criterian;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CommonUtil {

	public static String getCharBeforeEach(String param, String searchChar) {

		StringBuilder builder = new StringBuilder();

		List<String> splitString = new ArrayList<>();

		splitString.addAll(Arrays.asList(StringUtils.splitByWholeSeparator(param, searchChar)));

		splitString.remove(0);

		splitString.forEach(token -> {
			builder.append(StringUtils.left(token, 1));
		});

		return builder.toString();

	}

	public static Boolean hasMultiple(String param) {

		return (StringUtils.countMatches(param, Constants.COMMA.value()) > 0) ? true : false;

	}

	public static List<String> getValueAsList(String charSeparated, String splitChar) {
		return Arrays.asList(charSeparated.split(splitChar)).stream().map(word -> StringUtils.strip(word)).sorted()
				.collect(Collectors.toList());
	}

	public static <T> Boolean containsAll(List<T> objects, Criterian criterians) throws CommonException {

		MultiplePredicates<Object> predicates = new MultiplePredicates<>();

		// object -> readPropertyValue(object, propertyName) == value)

		criterians.getCriterias().stream().forEach(criteria -> {
			predicates.addPredicate(ExceptionWrapper.handlePredicate(
					object -> readPropertyValue(object, criteria.getCriteriaParam()) == criteria.getCriteriaValue()));
		});

		predicates.done();

		return objects.stream().anyMatch(predicates);

	}

	public static <T> Boolean contains(List<T> objects, String propertyName, Object value) throws CommonException {

		return objects.stream().anyMatch(ExceptionWrapper.handlePredicate(object -> {
			Object propValue = readPropertyValue(object, propertyName);
			if (propValue instanceof String) {
				return (propValue.toString().equalsIgnoreCase(value.toString()));
			} else
				return (propValue == value) ? true : false;
		}));
	}

	public static <T> Object readPropertyValue(T object, String property) throws CommonException {

		PropertyDescriptor pd;
		try {
			pd = new PropertyDescriptor(property, object.getClass());
			return pd.getReadMethod().invoke(object, new Object[] {});
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			log.debug("ERROR::CommonUtil::readPropertyValue - " + e.getStackTrace());
			throw new CommonException(e.getMessage());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
			// TODO Auto-generated catch block
			log.debug("ERROR::CommonUtil::readPropertyValue - " + e1.getStackTrace());
			throw new CommonException(e1.getMessage());
		}

	}

	public static String remove(String sourceString, String strToRemove) {

		return StringUtils.strip(StringUtils.remove(sourceString, strToRemove));

	}

	public static String extractStringBefore(String sourceString, String matchChar) {

		Integer startIndex = StringUtils.indexOf(sourceString, matchChar) - 1;

		return StringUtils.left(sourceString, startIndex);

	}

	public static String extractStringAfter(String sourceString, String matchChar) {

		Integer startIndex = StringUtils.indexOf(sourceString, matchChar) + 1;

		return StringUtils.substring(sourceString, startIndex);

	}

	public static String extractStringBetween(String condition, String matchStartChar, String matchEndChar) {

		Integer startIndex = StringUtils.indexOf(condition, matchStartChar) + 1;

		Integer endIndex = StringUtils.indexOf(condition, matchEndChar);

		String result = StringUtils.substring(condition, startIndex, endIndex);

		result = StringUtils.remove(result, Constants.DBL_QUOTE.value());

		return StringUtils.strip(result);

	}

	public static Boolean isValueExists(Object value) {

		return (value instanceof String) ? StringUtils.isNotBlank(value.toString()) : (value != null);
	}

	public static <T> Boolean isNotEmpty(List<T> list) {

		return (list != null && list.size() > 0);

	}

	public static <T> Boolean isEmpty(List<T> list) {
		return (list == null || list.size() == 0);
	}
	
	public static String replace (String sourceString, String charToReplace, String replaceChar) {
		
		return StringUtils.replace(sourceString, charToReplace, replaceChar);
	}
}
