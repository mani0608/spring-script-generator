package com.le.conversion.processor.sql;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.le.conversion.common.Constants;
import com.le.conversion.common.model.Param;
import com.le.conversion.model.ExecutionData;
import com.le.conversion.processor.sql.model.ConcatField;
import com.le.conversion.processor.sql.model.Condition;
import com.le.conversion.processor.sql.model.ImportTableQuery;
import com.le.conversion.processor.sql.model.Join;
import com.le.conversion.processor.sql.model.QueryField;
import com.le.conversion.processor.sql.model.SourceTableQuery;
import com.le.conversion.processor.util.ProcessorUtil;

import lombok.Setter;
import lombok.experimental.Accessors;

@Component("sqlProcessor")
@Accessors(fluent = true)
public class SQLProcessor {

	@Setter
	private ExecutionData executionData;

	protected void initializeQuery(String sourceTable, String sourceTableAlias) {
		SourceTableQuery query = SourceTableQuery.builder().sourceTable(sourceTable).sourceTableAlias(sourceTableAlias)
				.build();
		query.initializeList();
		executionData.queries().add(query);
	}

	protected void initializeImportQuery(String importTableName) {

		ImportTableQuery qImort = ImportTableQuery.builder().importTableName(importTableName).build();
		qImort.initializeList();
		getSTQ().getImportTables().add(qImort);

	}

	protected void createPrefixedSelectField(Param param) {

		QueryField qf = QueryField.builder().sourceAliasName(param.getColumnAlias())
				.caseWhenCandidates(new ArrayList<>()).sourceFieldName(param.getSourceColumn())
				.pkPrefix(param.getColumnPrefix()).importFieldName(param.getImportColumn()).build();

		if (executionData.queryIndex() == null || executionData.queryIndex() >= executionData.queries().size()) {
			SourceTableQuery query = SourceTableQuery.builder().build();
			query.initializeList();
			executionData.queries().add(query);
		}

		getITQ().getInsertFields().add(qf);

	}

	protected void createSelectField(Param param) {

		QueryField qf = QueryField.builder().sourceAliasName(param.getColumnAlias()).concatValue(param.getConcatValue())
				.concatCandidates(new ArrayList<>()).sourceFieldName(param.getSourceColumn())
				.caseWhenCandidates(new ArrayList<>()).importFieldName(param.getImportColumn())
				.isNewLineRequired(param.getIsNLReq()).build();

		if (executionData.queryIndex() == null || executionData.queryIndex() >= executionData.queries().size()) {
			SourceTableQuery query = SourceTableQuery.builder().build();
			query.initializeList();
			executionData.queries().add(query);
		}

		getITQ().getInsertFields().add(qf);
	}

	protected void createConcatField(String concatField, String concatValue, Boolean isNLReq) {

		if (executionData.queryIndex() == null || executionData.queryIndex() >= executionData.queries().size()) {
			SourceTableQuery query = SourceTableQuery.builder().build();
			query.initializeList();
			executionData.queries().add(query);
		}

		ConcatField cf = ConcatField.builder().fieldName(concatField).concatValue(concatValue)
				.isNewLineRequired(isNLReq).build();

		getQF().concatCandidates().add(cf);

	}

	protected void addInConditions(Param param) {

		if (executionData.queryIndex() == null || executionData.queryIndex() >= executionData.queries().size()) {
			SourceTableQuery query = SourceTableQuery.builder().build();
			query.initializeList();
			executionData.queries().add(query);
		}

		Condition condition = null;

		if (param.getConditionType().equals(Constants.CT_IN.value())) {
			condition = Condition.builder().inCondition(param.getInConditionValues()).field(param.getSourceColumn())
					.activeCondition(param.getConditionType()).isFieldRef(param.getIsFieldRef()).build();
		} else if (param.getConditionType().equals(Constants.CT_NOT_IN.value())) {
			condition = Condition.builder().notInCondition(param.getInConditionValues()).field(param.getSourceColumn())
					.activeCondition(param.getConditionType()).isFieldRef(param.getIsFieldRef()).build();
		}

		if (param.getIsJoinCondition()) {
			Join join = Join.builder().tableName(param.getTableName()).tableAlias(param.getTableAlias()).build();
			if (join.getJoinConditions() == null) {
				join.initializeList();
			}
			join.getJoinConditions().add(condition);
			if (!ProcessorUtil.isDuplicateJoin(getITQ().getTableJoins(), join)) {
				getITQ().getTableJoins().add(join);
			}
		} else if (param.getIsCaseCondition()) {
			if (!ProcessorUtil.isDuplicateCondition(getQF().caseWhenCandidates(), condition)) {
				getQF().caseWhenCandidates().add(condition);
			}
		} else {
			if (!ProcessorUtil.isDuplicateCondition(getITQ().getWhereConditions(), condition)) {
				getITQ().getWhereConditions().add(condition);
			}
		}

	}

	protected void addLikeCondition(Param param) {

		if (executionData.queryIndex() == null || executionData.queryIndex() >= executionData.queries().size()) {
			SourceTableQuery query = SourceTableQuery.builder().build();
			query.initializeList();
			executionData.queries().add(query);
		}

		Condition condition = null;

		if (param.getConditionType().equals(Constants.CT_LIKE.value())) {
			condition = Condition.builder().likeCondition(param.getConditionValue()).field(param.getSourceColumn())
					.activeCondition(param.getConditionType()).isFieldRef(param.getIsFieldRef()).build();
		} else if (param.getConditionType().equals(Constants.CT_NOT_LIKE.value())) {
			condition = Condition.builder().notLikeCondition(param.getConditionValue()).field(param.getSourceColumn())
					.activeCondition(param.getConditionType()).isFieldRef(param.getIsFieldRef()).build();
		}

		if (param.getIsJoinCondition()) {
			Join join = Join.builder().tableName(param.getTableName()).tableAlias(param.getTableAlias()).build();
			if (join.getJoinConditions() == null) {
				join.initializeList();
			}
			join.getJoinConditions().add(condition);
			if (!ProcessorUtil.isDuplicateJoin(getITQ().getTableJoins(), join)) {
				getITQ().getTableJoins().add(join);
			}
		} else if (param.getIsCaseCondition()) {
			if (!ProcessorUtil.isDuplicateCondition(getQF().caseWhenCandidates(), condition)) {
				getQF().caseWhenCandidates().add(condition);
			}
		} else {
			if (!ProcessorUtil.isDuplicateCondition(getITQ().getWhereConditions(), condition)) {
				getITQ().getWhereConditions().add(condition);
			}
		}

	}

	protected void addEqualsCondition(Param param) {

		if (executionData.queryIndex() == null || executionData.queryIndex() >= executionData.queries().size()) {
			SourceTableQuery query = SourceTableQuery.builder().build();
			query.initializeList();
			executionData.queries().add(query);
		}

		Condition condition = null;

		if (param.getConditionType().equals(Constants.CT_EQ.value())) {
			condition = Condition.builder().eqCondition(param.getConditionValue()).field(param.getSourceColumn())
					.activeCondition(param.getConditionType()).isFieldRef(param.getIsFieldRef()).build();
		} else if (param.getConditionType().equals(Constants.CT_NOT_EQ.value())) {
			condition = Condition.builder().neqCondition(param.getConditionValue()).field(param.getSourceColumn())
					.activeCondition(param.getConditionType()).isFieldRef(param.getIsFieldRef()).build();
		}

		if (param.getIsJoinCondition()) {
			Join join = Join.builder().tableName(param.getTableName()).tableAlias(param.getTableAlias()).build();
			if (join.getJoinConditions() == null) {
				join.initializeList();
			}
			join.getJoinConditions().add(condition);
			if (!ProcessorUtil.isDuplicateJoin(getITQ().getTableJoins(), join)) {
				getITQ().getTableJoins().add(join);
			}
		} else if (param.getIsCaseCondition()) {
			if (!ProcessorUtil.isDuplicateCondition(getQF().caseWhenCandidates(), condition)) {
				getQF().caseWhenCandidates().add(condition);
			}
		} else {
			if (!ProcessorUtil.isDuplicateCondition(getITQ().getWhereConditions(), condition)) {
				getITQ().getWhereConditions().add(condition);
			}
		}

	}

	protected void addNullCondition(Param param) {

		if (executionData.queryIndex() == null || executionData.queryIndex() >= executionData.queries().size()) {
			SourceTableQuery query = SourceTableQuery.builder().build();
			query.initializeList();
			executionData.queries().add(query);
		}

		Condition condition = null;

		if (param.getConditionType().equals(Constants.CT_NULL.value())) {
			condition = Condition.builder().isNullCondition(true).isNotNullCondition(false)
					.activeCondition(param.getConditionType()).field(param.getSourceColumn())
					.isFieldRef(param.getIsFieldRef()).build();
		} else if (param.getConditionType().equals(Constants.CT_NOT_NULL.value())) {
			condition = Condition.builder().isNullCondition(false).isNotNullCondition(true)
					.activeCondition(param.getConditionType()).field(param.getSourceColumn())
					.isFieldRef(param.getIsFieldRef()).build();
		}

		if (param.getIsJoinCondition()) {
			Join join = Join.builder().tableName(param.getTableName()).tableAlias(param.getTableAlias()).build();
			if (join.getJoinConditions() == null) {
				join.initializeList();
			}
			join.getJoinConditions().add(condition);
			if (!ProcessorUtil.isDuplicateJoin(getITQ().getTableJoins(), join)) {
				getITQ().getTableJoins().add(join);
			}
		} else if (param.getIsCaseCondition()) {
			if (!ProcessorUtil.isDuplicateCondition(getQF().caseWhenCandidates(), condition)) {
				getQF().caseWhenCandidates().add(condition);
			}
		} else {
			if (!ProcessorUtil.isDuplicateCondition(getITQ().getWhereConditions(), condition)) {
				getITQ().getWhereConditions().add(condition);
			}
		}

	}

	private SourceTableQuery getSTQ() {
		return executionData.queries().get(executionData.queryIndex());
	}

	private ImportTableQuery getITQ() {
		return executionData.queries().get(executionData.queryIndex()).getImportTables()
				.get(executionData.importQueryIndex());
	}

	private QueryField getQF() {
		return getITQ().getInsertFields().get(executionData.importFieldIndex());
	}

}
