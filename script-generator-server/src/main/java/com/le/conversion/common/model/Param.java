package com.le.conversion.common.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Param {

	private String sourceColumn;
	
	private String importColumn;
	
	private String columnAlias;
	
	private String columnPrefix;
	
	private Object concatValue;
	
	private Boolean isNLReq;
	
	private String tableName;
	
	private String tableAlias;
	
	private String concatField;
	
	private List<String> inConditionValues;
	
	private String conditionValue;
	
	private String conditionType;
	
	private Boolean isFieldRef;
	
	private Boolean isJoinCondition;
	
	private Boolean isCaseCondition;
	
}
