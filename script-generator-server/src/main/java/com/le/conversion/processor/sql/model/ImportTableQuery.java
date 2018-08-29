package com.le.conversion.processor.sql.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ImportTableQuery {
	
	private String importTableName;
	
	private List<QueryField> insertFields;
	
	private List<Condition> whereConditions;
	
	private List<Join> tableJoins;
	
	private List<String> groupByFields;
	
	private List<Condition> havingConditions;
	
	public void initializeList() {
		
		insertFields = new ArrayList<>();
		
		whereConditions = new ArrayList<>();
		
		tableJoins = new ArrayList<>();
		
		groupByFields = new ArrayList<>();
		
		havingConditions = new ArrayList<>();
		
	}

}
