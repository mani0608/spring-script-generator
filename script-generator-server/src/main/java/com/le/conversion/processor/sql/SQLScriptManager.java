package com.le.conversion.processor.sql;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.le.conversion.common.Columns;
import com.le.conversion.common.Constants;
import com.le.conversion.common.model.Param;
import com.le.conversion.common.util.CommonUtil;
import com.le.conversion.model.DestinationColumns;
import com.le.conversion.model.DestinationTableMapping;
import com.le.conversion.model.ExecutionData;
import com.le.conversion.model.MappingProperties;
import com.le.conversion.model.RelationshipColumns;
import com.le.conversion.model.SourceTableMapping;
import com.le.conversion.processor.util.ProcessorUtil;

import lombok.Setter;
import lombok.experimental.Accessors;

@Component ("sqlScriptManager")
@Accessors(fluent = true)
public class SQLScriptManager {
	
	@Autowired
	private SQLProcessor sqlProcessor;
	
	@Setter	private ExecutionData executionData;

	private String sourceTable;

	private String primaryKeyCol;

	public void createSQL(SourceTableMapping mapping, Integer queryIndex) {

		sqlProcessor.executionData(executionData);
		
		sourceTable = mapping.getSourceTable();

		primaryKeyCol = mapping.getPrimaryKeyCol();

		executionData.queryIndex(queryIndex);

		sqlProcessor.initializeQuery(sourceTable, ProcessorUtil.getAlias(sourceTable));

		IntStream.range(0, mapping.getDestTableMappings().size()).forEach(idx -> {

			executionData.importQueryIndex(idx);

			DestinationTableMapping dtMapping = mapping.getDestTableMappings().get(idx);

			sqlProcessor.initializeImportQuery(dtMapping.getDestinationTable());

			Param param = Param.builder().sourceColumn(ProcessorUtil.getAliasedField(primaryKeyCol, sourceTable))
					.importColumn(Columns.INSERT_COL_ID.col())
					.columnPrefix(ProcessorUtil.generatePrefix(dtMapping.getDestinationTable()))
					.columnAlias(Columns.PK_ALIAS.col()).build();

			sqlProcessor.createPrefixedSelectField(param);

			param = Param.builder().importColumn(Columns.INSERT_COL_TRID.col()).concatValue(new Integer(1))
					.columnAlias(Columns.TRID_ALIAS.col()).isNLReq(false).build();

			sqlProcessor.createSelectField(param);

			IntStream.range(0, dtMapping.getMappingProps().size()).forEach(pIdx -> {

				executionData.importFieldIndex(pIdx + 2);

				MappingProperties prop = dtMapping.getMappingProps().get(pIdx);
				generateScript(prop, dtMapping.getDestinationTable());
				ProcessorUtil.findConcatCandidates(dtMapping, prop).stream().forEach(candProp -> {
					generateConcatScript(candProp, dtMapping.getDestinationTable());
				});
			});

		});

	}

	private void generateScript(MappingProperties prop, String destTable) {

		if (!prop.getIsProcessed()) {

			// Processing non-relationship rows
			if (!isRelationship(prop.getDestColumns())) {
				if (StringUtils.isNotBlank(prop.getDestValue())) {
					formatAndCreateSelectField(prop.getDestColumn(), prop.getDestValue(), prop.getSourceColumn());
				} else {

					Param param = Param.builder()
							.sourceColumn(ProcessorUtil.getAliasedField(prop.getSourceColumn(), sourceTable))
							.importColumn(prop.getDestColumn())
							.columnAlias(ProcessorUtil.getAlias(prop.getDestColumn())).isNLReq(false).build();

					sqlProcessor.createSelectField(param);
				}

			} else {

				createSourceAndDestFields(prop);
			}

			if (prop.getJoinTable() != null && prop.getJoinTable().length() > 0) {
				parseTableJoins(prop.getJoinTable());
			}

			String nonRelationalConditions = ProcessorUtil.getNonRelationalConditions(prop.getConditions());

			if (CommonUtil.isValueExists(nonRelationalConditions)) {

				Param condParam = Param.builder().sourceColumn(prop.getSourceColumn())
						.conditionValue(nonRelationalConditions).isFieldRef(false).isJoinCondition(false)
						.isCaseCondition(false).build();

				parseAndCreateConditions(condParam);
			}

			prop.setIsProcessed(true);

		}

	}

	private void generateConcatScript(MappingProperties prop, String destTable) {

		String destValue = StringUtils.remove(prop.getDestValue(), Constants.DEST_VAL_PH.value());

		destValue = StringUtils.remove(destValue, Constants.NEWLINE_PH.value());

		destValue = StringUtils.remove(destValue, Constants.PLUS.value());

		destValue = StringUtils.remove(destValue, Constants.DBL_QUOTE.value());

		sqlProcessor.createConcatField(ProcessorUtil.getAliasedField(prop.getSourceColumn(), sourceTable),
				StringUtils.strip(destValue), true);

		prop.setIsProcessed(true);

	}

	/**
	 * Does the give row contain a relationship mapping?
	 * 
	 * @param columns
	 * @return
	 */
	private Boolean isRelationship(DestinationColumns<String> columns) {

		return (columns.getColumnsCount() > 1);

	}

	private void formatAndCreateSelectField(String destColumn, String value, String sourceColumn) {

		String destValue = value;

		Boolean isNLReq = false;

		Param param = null;

		if (StringUtils.contains(destValue, Constants.DEST_VAL_PH.value())) {

			destValue = StringUtils.remove(destValue, Constants.DEST_VAL_PH.value());

			if (StringUtils.contains(destValue, Constants.NEWLINE_PH.value())) {

				destValue = StringUtils.remove(destValue, Constants.NEWLINE_PH.value());

				isNLReq = true;
			}

			destValue = StringUtils.remove(destValue, Constants.PLUS.value());

			destValue = StringUtils.remove(destValue, Constants.DBL_QUOTE.value());

			param = Param.builder().sourceColumn(ProcessorUtil.getAliasedField(sourceColumn, sourceTable))
					.importColumn(destColumn).concatValue(StringUtils.strip(destValue))
					.columnAlias(ProcessorUtil.getAlias(destColumn)).isNLReq(isNLReq).build();

			sqlProcessor.createSelectField(param);

		} else {

			destValue = StringUtils.remove(destValue, Constants.DBL_QUOTE.value());

			param = Param.builder().importColumn(destColumn).concatValue(StringUtils.strip(destValue))
					.columnAlias(ProcessorUtil.getAlias(destColumn)).isNLReq(isNLReq).build();

			sqlProcessor.createSelectField(param);
		}

	}

	private void createSourceAndDestFields(MappingProperties prop) {

		Param param = null;

		RelationshipColumns relColumns = ProcessorUtil.extractSourceAndDestColumns(prop.getDestColumns().getColumns());

		if (StringUtils.contains(prop.getSourceColumn(), Constants.SRC_KEY_PH.value())) {

			param = Param.builder()
					.sourceColumn(ProcessorUtil.getAliasedField(
							CommonUtil.remove(prop.getSourceColumn(), Constants.SRC_KEY_PH.value()), sourceTable))
					.importColumn(relColumns.getRelSourceTable())
					.columnPrefix(ProcessorUtil.generatePrefix(relColumns.getFormattedRelSource()))
					.columnAlias(ProcessorUtil.getRelationAlias(relColumns.getFormattedRelSource(),
							Constants.SRC_REl_ALIAS_PFX.value()))
					.build();

			sqlProcessor.createPrefixedSelectField(param);

		} else {

			param = Param.builder().sourceColumn(ProcessorUtil.getAliasedField(primaryKeyCol, sourceTable))
					.importColumn(relColumns.getRelSourceTable())
					.columnPrefix(ProcessorUtil.generatePrefix(relColumns.getFormattedRelSource()))
					.columnAlias(ProcessorUtil.getRelationAlias(relColumns.getFormattedRelSource(),
							Constants.SRC_REl_ALIAS_PFX.value()))
					.build();

			sqlProcessor.createPrefixedSelectField(param);
		}

		if (StringUtils.contains(prop.getSourceColumn(), Constants.REL_COND_PH.value())
				|| StringUtils.contains(prop.getSourceColumn(), Constants.SRC_KEY_PH.value())) {

			String srcColumn = CommonUtil.remove(prop.getSourceColumn(), Constants.REL_COND_PH.value());

			srcColumn = CommonUtil.remove(srcColumn, Constants.SRC_KEY_PH.value());

			param = Param.builder().sourceColumn(ProcessorUtil.getAliasedField(srcColumn, sourceTable))
					.conditionType(Constants.getCondType(Constants.NOT_NULL_PH.value())).isFieldRef(false)
					.isJoinCondition(false).isCaseCondition(false).build();

			sqlProcessor.addNullCondition(param);
		}

		IntStream.range(0, relColumns.getRelTargetTables().size()).forEach(idx -> {

			executionData.importFieldIndex(executionData.importFieldIndex() + 1);

			String tgt = relColumns.getRelTargetAtIndex(idx);

			Param dstParam = Param.builder().sourceColumn(ProcessorUtil.getAliasedField(primaryKeyCol, sourceTable))
					.importColumn(tgt)
					.columnPrefix(ProcessorUtil.generatePrefix(relColumns.getFormattedRelTargetAtIndex(idx)))
					.columnAlias(ProcessorUtil.getRelationAlias(relColumns.getFormattedRelTargetAtIndex(idx),
							Constants.TGT_REl_ALIAS_PFX.value()))
					.build();

			sqlProcessor.createPrefixedSelectField(dstParam);

			if (prop.getConditions() != null
					&& StringUtils.contains(prop.getConditions(), Constants.REL_CASE_WHEN_PH.value())) {
				parseRelCaseWhenConditions(prop.getConditions(), idx);
			}

		});

	}

	private void parseAndCreateConditions(Param condParam) {

		List<String> conditions = CommonUtil.getValueAsList(condParam.getConditionValue(), Constants.AND_PH.value());

		conditions.forEach(cnd -> {

			Constants.PLHDRS.stream().filter(ph -> cnd.contains(ph.value())).forEach(ct -> {

				switch (ct) {

				case IN_PH:

					sqlProcessor.addInConditions(generateInConditionParam(cnd, condParam, Constants.IN_PH.value()));

					break;

				case NOT_IN_PH:

					sqlProcessor.addInConditions(generateInConditionParam(cnd, condParam, Constants.NOT_IN_PH.value()));

					break;

				case LIKE_PH:

					sqlProcessor.addLikeCondition(generateConditionParam(cnd, condParam, Constants.LIKE_PH.value()));

					break;

				case NOT_LIKE_PH:

					sqlProcessor.addLikeCondition(generateConditionParam(cnd, condParam, Constants.NOT_LIKE_PH.value()));

					break;

				case EQ_PH:

					sqlProcessor.addEqualsCondition(generateConditionParam(cnd, condParam, Constants.EQ_PH.value()));

					break;

				case NEQ_PH:

					sqlProcessor.addEqualsCondition(generateConditionParam(cnd, condParam, Constants.NEQ_PH.value()));

					break;

				case NULL_PH:

					sqlProcessor.addNullCondition(generateConditionParam(cnd, condParam, Constants.NULL_PH.value()));

					break;

				case NOT_NULL_PH:

					sqlProcessor.addNullCondition(generateConditionParam(cnd, condParam, Constants.NOT_NULL_PH.value()));

					break;

				default:
					break;
				}

			});

		});
	}

	private Param generateInConditionParam(String condition, Param condParam, String conditionType) {

		Param param = null;

		String conditionValue = ProcessorUtil.extractConditionValue(condition, conditionType);

		List<String> conditionValues = Arrays.asList(conditionValue.split(Constants.COMMA.value()));

		if (StringUtils.contains(condition, Constants.SRC_VAL_PH.value())) {

			param = Param.builder()
					.sourceColumn(ProcessorUtil.getAliasedField(condParam.getSourceColumn(), sourceTable))
					.inConditionValues(conditionValues).conditionType(Constants.getCondType(conditionType))
					.tableName(condParam.getTableName()).tableAlias(condParam.getTableAlias())
					.isFieldRef(condParam.getIsFieldRef()).isCaseCondition(condParam.getIsCaseCondition())
					.isJoinCondition(condParam.getIsJoinCondition()).build();
		} else {

			String conditionField = ProcessorUtil.extractConditionField(condition, conditionType);

			param = Param.builder().sourceColumn(conditionField).inConditionValues(conditionValues)
					.conditionType(Constants.getCondType(conditionType)).tableName(condParam.getTableName())
					.tableAlias(condParam.getTableAlias()).isFieldRef(condParam.getIsFieldRef())
					.isCaseCondition(condParam.getIsCaseCondition()).isJoinCondition(condParam.getIsJoinCondition())
					.build();

		}

		return param;

	}

	private Param generateConditionParam(String condition, Param condParam, String conditionType) {

		Param param = null;

		String conditionValue = ProcessorUtil.extractConditionValue(condition, conditionType);

		if (StringUtils.contains(condition, Constants.SRC_VAL_PH.value())) {

			param = Param.builder()
					.sourceColumn(ProcessorUtil.getAliasedField(condParam.getSourceColumn(), sourceTable))
					.conditionValue(conditionValue).conditionType(Constants.getCondType(conditionType))
					.tableName(condParam.getTableName()).tableAlias(condParam.getTableAlias())
					.isCaseCondition(condParam.getIsCaseCondition()).isFieldRef(condParam.getIsFieldRef())
					.isJoinCondition(condParam.getIsJoinCondition()).build();

		} else {

			String conditionField = ProcessorUtil.extractConditionField(condition, conditionType);

			param = Param.builder().sourceColumn(conditionField).conditionValue(conditionValue)
					.conditionType(Constants.getCondType(conditionType)).tableName(condParam.getTableName())
					.tableAlias(condParam.getTableAlias()).isFieldRef(condParam.getIsFieldRef())
					.isCaseCondition(condParam.getIsCaseCondition()).isJoinCondition(condParam.getIsJoinCondition())
					.build();
		}

		return param;

	}

	private void parseTableJoins(String joinTable) {

		List<String> tableConfig = CommonUtil.getValueAsList(joinTable, Constants.SEMI_COLON.value());

		Param param = Param.builder().tableName(tableConfig.get(1)).conditionValue(tableConfig.get(0))
				.tableAlias(ProcessorUtil.getAlias(tableConfig.get(1))).isFieldRef(true).isJoinCondition(true)
				.isCaseCondition(false).build();

		parseAndCreateConditions(param);

	}

	private void parseRelCaseWhenConditions(String caseWhen, Integer targetIndex) {

		List<String> conditions = CommonUtil.getValueAsList(caseWhen, Constants.AND_PH.value());

		conditions.stream().forEach(cond -> {

			if (StringUtils.contains(cond, Constants.REL_CASE_WHEN_PH.value())) {

				String condition = StringUtils.remove(cond, Constants.REL_CASE_WHEN_PH.value());

				List<String> conditionValues = ProcessorUtil.extractCaseWhenValueASList(condition,
						Constants.OPEN_PARAN.value(), Constants.CLOSE_PARAN.value());

				String conditionForTarget = CommonUtil.extractStringBefore(condition, Constants.OPEN_PARAN.value())
						+ Constants.SPACE.value() + conditionValues.get(targetIndex);

				Param param = Param.builder().conditionValue(conditionForTarget).isCaseCondition(true)
						.isJoinCondition(false).isFieldRef(false).build();

				parseAndCreateConditions(param);
			}

		});

	}

}
