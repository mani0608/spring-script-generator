package com.le.conversion.processor.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import com.le.conversion.common.Columns;
import com.le.conversion.common.Constants;
import com.le.conversion.common.util.CommonUtil;
import com.le.conversion.model.DestinationTableMapping;
import com.le.conversion.model.MappingProperties;
import com.le.conversion.model.RelationshipColumns;
import com.le.conversion.processor.sql.model.Condition;
import com.le.conversion.processor.sql.model.Join;
import com.le.conversion.processor.sql.model.QueryField;

public class ProcessorUtil {

	public static List<MappingProperties> findConcatCandidates(DestinationTableMapping dtMapping,
			MappingProperties mProp) {

		Optional<List<MappingProperties>> optResult = dtMapping.getMappingProps().stream()
				.filter(prop -> (!prop.getIsProcessed() && prop.getDestColumns().getColumnsCount() == 1))
				.filter(prop -> prop.getDestColumn().equalsIgnoreCase(mProp.getDestColumn()))
				.collect(Collectors.groupingBy(MappingProperties::getDestColumn)).values().stream()
				.filter(value -> (value.size() > 1)).findFirst();

		List<MappingProperties> result = optResult.isPresent() ? optResult.get() : Collections.emptyList();

		return result.stream().filter(mp -> !EqualsBuilder.reflectionEquals(mp, mProp, false))
				.collect(Collectors.toList());
	}

	public static String extractConditionValue(String conditionExpression, String conditionType) {

		Integer condTypeIdx = StringUtils.indexOf(conditionExpression, conditionType);

		String conditionValue = StringUtils.substring(conditionExpression, condTypeIdx + conditionType.length());

		conditionValue = StringUtils.strip(conditionValue);

		if (Constants.MULTIVALUES.contains(Constants.getEnum(conditionType))) {
			conditionValue = StringUtils.remove(conditionValue, Constants.OPEN_PARAN.value());
			conditionValue = StringUtils.remove(conditionValue, Constants.CLOSE_PARAN.value());
		}

		conditionValue = StringUtils.remove(conditionValue, Constants.SINGLE_QUOTE.value());

		return StringUtils.strip(conditionValue);

	}

	public static String extractConditionField(String conditionExpression, String conditionType) {

		Integer condTypeIdx = StringUtils.indexOf(conditionExpression, conditionType);

		String conditionValue = StringUtils.left(conditionExpression, condTypeIdx);

		return StringUtils.strip(conditionValue);

	}

	public static RelationshipColumns extractSourceAndDestColumns(List<String> destColumns) {

		RelationshipColumns relColumns = RelationshipColumns.builder().build();

		relColumns.setRelSourceTable(destColumns.stream().map(col -> StringUtils.strip(col))
				.filter(col -> col.startsWith(Columns.SRC_TBL_PFX.col())).findFirst().get());

		relColumns.setRelTargetTables(destColumns.stream().map(col -> StringUtils.strip(col))
				.filter(col -> col.startsWith(Columns.DEST_TBL_PFX.col())).collect(Collectors.toList()));

		relColumns.setFormattedRelSource(CommonUtil.remove(relColumns.getRelSourceTable(), Columns.SRC_TBL_PFX.col()));

		relColumns.setFormattedRelTargets(relColumns.getRelTargetTables().stream()
				.map(col -> CommonUtil.remove(col, Columns.DEST_TBL_PFX.col())).collect(Collectors.toList()));

		return relColumns;

	}

	public static String getAliasedField(String field, String table) {

		return new StringBuilder().append(getAlias(table)).append(Constants.PERIOD.value()).append(field).toString();

	}

	public static String getAlias(String param) {

		String alias;

		param = StringUtils.removeEnd(param, Constants.UNDERSCORE.value());

		if (param.length() <= 3) {
			return param.toUpperCase();
		}

		alias = StringUtils.left(param, 1);

		if (StringUtils.contains(param, Constants.UNDERSCORE.value())) {
			alias += CommonUtil.getCharBeforeEach(param, Constants.UNDERSCORE.value());
		} else {
			alias += StringUtils.mid(param, (param.length() / 2), 1);
			alias += StringUtils.right(param, 1);
		}

		return alias.toUpperCase();
	}

	public static String generatePrefix(String table) {

		StringBuilder pkPrefix = new StringBuilder();

		pkPrefix.append(getAlias(table)).append(Constants.UNDERSCORE.value());

		return pkPrefix.toString();

	}

	public static String getRelationAlias(String sourceReltable, String prefix) {

		StringBuilder result = new StringBuilder();

		return result.append(prefix).append(Constants.UNDERSCORE.value()).append(getAlias(sourceReltable)).toString();

	}

	public static List<String> extractCaseWhenValueASList(String condition, String matchStartChar,
			String matchEndChar) {

		return Arrays.asList(StringUtils.split(CommonUtil.extractStringBetween(condition, matchStartChar, matchEndChar),
				Constants.COMMA.value()));

	}

	public static Boolean isLikeCondition(String condType) {

		return (Constants.LIKES.contains(Constants.getEnum(condType)));
	}

	public static Boolean isConcatRequired(QueryField qf) {

		return ((CommonUtil.isValueExists(qf.concatValue()) && (CommonUtil.isValueExists(qf.sourceFieldName())))
				|| CommonUtil.isNotEmpty(qf.concatCandidates()));

	}

	public static Boolean isDuplicateJoin(List<Join> joins, Join newJoin) {

		if (CommonUtil.isEmpty(joins))
			return false;

		// return (joins.stream().filter(jn -> (jn == newJoin)).count() > 0);
		return (joins.contains(newJoin));

	}

	public static Boolean isDuplicateCondition(List<Condition> conditions, Condition condition) {

		if (CommonUtil.isEmpty(conditions))
			return false;

		// return (joins.stream().filter(jn -> (jn == newJoin)).count() > 0);
		return (conditions.contains(condition));

	}
	
	public static String getNonRelationalConditions (String conditionStr) {
		
		List<String> conditions = CommonUtil.getValueAsList(conditionStr, Constants.AND_PH.value());
		
		return (conditions.stream().filter(cond -> !cond.contains(Constants.REL_COND_PFX.value())).collect(Collectors.joining(Constants.AND_PH.value())));
		
	}
}
