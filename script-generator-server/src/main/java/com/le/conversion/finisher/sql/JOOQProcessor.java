package com.le.conversion.finisher.sql;

import static org.jooq.impl.DSL.concat;
import static org.jooq.impl.DSL.decode;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.trueCondition;
import static org.jooq.impl.DSL.val;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.GroupField;
import org.jooq.Param;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.conf.ParamType;

import com.le.conversion.common.Constants;
import com.le.conversion.common.util.CommonUtil;
import com.le.conversion.finisher.sql.model.JOOQSourceTableQuery;
import com.le.conversion.processor.sql.model.Condition;
import com.le.conversion.processor.sql.model.Join;
import com.le.conversion.processor.sql.model.QueryField;
import com.le.conversion.processor.sql.model.SourceTableQuery;
import com.le.conversion.processor.util.ProcessorUtil;

public class JOOQProcessor {

	private DSLContext creator;

	protected JOOQProcessor(DSLContext creator) {
		this.creator = creator;
	}

	public static JOOQProcessor getInstance(DSLContext creator) {
		return new JOOQProcessor(creator);
	}

	public JOOQSourceTableQuery generateScript(SourceTableQuery query) {

		JOOQSourceTableQuery jooqSrcTableQuery = JOOQSourceTableQuery.builder().build();

		jooqSrcTableQuery.initialize();

		query.getImportTables().stream().forEach(itq -> {

			String resultQuery = creator
					.insertInto(getJOOQTable(itq.getImportTableName(), null), getInsertFields(itq.getInsertFields()))
					.select(creator.select(getJOOQSelect(itq.getInsertFields()))
							// .from(getJOOQTable(query.getSourceTable(), query.getSourceTableAlias()))
							.from(generateFrom(query.getSourceTable(), query.getSourceTableAlias(),
									itq.getTableJoins()))
							.where(buildCondition(itq.getWhereConditions()))
							.groupBy(buildGroupBy(itq.getGroupByFields()))
							.having(buildCondition(itq.getHavingConditions())))
					.getSQL(ParamType.INLINED);
			
			resultQuery = CommonUtil.remove(resultQuery, Constants.DBL_QUOTE.value());
			
			resultQuery = CommonUtil.replace(resultQuery, Constants.CONCAT_PH.value(), Constants.PLUS.value());
			
			jooqSrcTableQuery.getImportQueries().add(resultQuery);

		});

		return jooqSrcTableQuery;
	}

	private Table<?> generateFrom(String sourceTable, String sourceAlias, List<Join> joinTables) {

		if (CommonUtil.isNotEmpty(joinTables)) {

			return joinTables.stream().map(jt -> {

				Table<?> tableJoins = getJOOQTable(sourceTable, sourceAlias);

				tableJoins = tableJoins.join(getJOOQTable(jt.getTableName(), jt.getTableAlias()))
						.on(buildCondition(jt.getJoinConditions()));

				return tableJoins;
			}).findFirst().get();
		} else {
			return getJOOQTable(sourceTable, sourceAlias);
		}

	}

	private List<Field<String>> getInsertFields(List<QueryField> importFields) {

		return importFields.stream().map(qf -> getJOOQField(qf.importFieldName())).collect(Collectors.toList());

	}

	private List<SelectField<String>> getJOOQSelect(List<QueryField> importFields) {

		List<SelectField<String>> selectFields = new ArrayList<>();

		List<Field<String>> concatFields = new ArrayList<>();
		List<Field<String>> tempFields = new ArrayList<>();

		importFields.forEach(qf -> {

			concatFields.clear();

			tempFields.clear();

			Field<String> selectField = null;

			if (ProcessorUtil.isConcatRequired(qf)) {

				if (CommonUtil.isValueExists(qf.concatValue())) {
					tempFields.add(getValField(qf.concatValue()));
				}

				if (CommonUtil.isValueExists(qf.sourceFieldName())) {
					tempFields.add(getJOOQField(qf.sourceFieldName()));
				}

				if (CommonUtil.isNotEmpty(qf.concatCandidates())) {

					tempFields.add(getValField(Constants.NL_CHAR_PH.value()));

					concatFields.add(generateDecode(tempFields, qf.sourceFieldName()));

					concatFields.addAll(qf.concatCandidates().stream()
							.collect(Collector.of(() -> new ArrayList<Field<String>>(), (l, cf) -> {

								List<Field<String>> localList = new ArrayList<>();

								if (CommonUtil.isValueExists(cf.concatValue())) {
									localList.add(getValField(cf.concatValue()));
								}

								localList.add(getJOOQField(cf.fieldName()));

								if (cf.isNewLineRequired()) {
									localList.add(getValField(Constants.NL_CHAR_PH.value()));
								}

								l.add(generateDecode(localList, cf.fieldName()));

							}, (l1, l2) -> {
								l1.addAll(l2);
								return l1;
							}, Function.identity())));
				} else {

					concatFields.add(generateDecode(tempFields, qf.sourceFieldName()));
				}

				//selectField = concat(concatFields.stream().toArray(size -> new Field[size]));
				selectField = concat(getFieldArray(concatFields));

			} else {

				if (CommonUtil.isValueExists(qf.pkPrefix())) {
					selectField = getValField(qf.pkPrefix());
				}

				if (CommonUtil.isValueExists(qf.sourceFieldName())) {
					if (selectField != null)
						selectField = selectField.concat(getJOOQField(qf.sourceFieldName()));
					else
						selectField = getJOOQField(qf.sourceFieldName());
				}

				if (CommonUtil.isValueExists(qf.concatValue())) {
					selectField = getValField(qf.concatValue());
				}

			}

			if (CommonUtil.isNotEmpty(qf.caseWhenCandidates())) {

				selectField = decode().when(buildCondition(qf.caseWhenCandidates()), selectField)
						.as(qf.sourceAliasName());

			} else {

				selectField = selectField.as(qf.sourceAliasName());
			}

			selectFields.add(selectField);

		});

		return selectFields;

	}
	
	private Field<?>[] getFieldArray (List<Field<String>> fields) {
		Field<?>[] fieldArr = new Field[fields.size()];
		
		return fields.toArray(fieldArr);
		
	}

	private List<GroupField> buildGroupBy(List<String> fields) {

		return fields.stream().map(f -> getJOOQField(f)).collect(Collectors.toList());

	}

	private Field<String> generateDecode(List<Field<String>> fields, String sourceFieldName) {

		//Field<String> concatField = concat(fields.stream().toArray(size -> new Field[size]));
		Field<String> concatField = concat(getFieldArray(fields));

		return decode().when(getJOOQField(sourceFieldName).isNotNull(), concatField).otherwise("");

	}

	private Param<String> getValField(Object value) {

		return val(value.toString());

	}

	private Field<String> getJOOQField(String fieldName) {
		return field(name(fieldName), String.class);

	}

	private Table<?> getJOOQTable(String tableName, String tableAlias) {
		return (CommonUtil.isValueExists(tableAlias)) ? table(name(tableName)).as(tableAlias) : table(name(tableName));
	}

	private org.jooq.Condition buildCondition(List<Condition> conditions) {

		org.jooq.Condition jc = trueCondition();

		for (Condition cond : conditions) {

			Field<String> caseField = getJOOQField(cond.getField());

			switch (Constants.getEnum(cond.getActiveCondition())) {

			case CT_IN:
				jc = jc.and(caseField.in(cond.getInCondition()));
				break;
			case CT_NOT_IN:
				jc = jc.and(caseField.notIn(cond.getNotInCondition()));
				break;
			case CT_LIKE:
				jc = jc.and(caseField.like(cond.getLikeCondition()));
				break;
			case CT_NOT_LIKE:
				jc = jc.and(caseField.notLike(cond.getNotLikeCondition()));
				break;
			case CT_EQ:
				jc = jc.and(caseField.eq(cond.getEqCondition()));
				break;
			case CT_NOT_EQ:
				jc = jc.and(caseField.ne(cond.getNeqCondition()));
				break;
			case CT_NULL:
				jc = jc.and(caseField.isNull());
				break;
			case CT_NOT_NULL:
				jc = jc.and(caseField.isNotNull());
				break;
			default:
				break;
			}

		}

		return jc;

	}

}
