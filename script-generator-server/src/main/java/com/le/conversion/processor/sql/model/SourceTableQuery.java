package com.le.conversion.processor.sql.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SourceTableQuery {

	private String sourceTable;
	
	private String sourceTableAlias;
	
	private List<ImportTableQuery> importTables;
	
	public void initializeList() {
		
		importTables = new ArrayList<>();
		
	}
	
}
