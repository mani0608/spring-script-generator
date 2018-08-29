package com.le.conversion.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MappingProperties {
	
	private String sourceColumn;

	private DestinationColumns<String> destColumns;

	private String destValue;
	
	private String joinTable;

	private String conditions;
	
	private Boolean isProcessed;
	
	//Applicable only for non relational mapping
	public String getDestColumn() {
		return this.destColumns.get(0);
	}

}
