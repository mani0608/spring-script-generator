package com.le.conversion.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RelationshipColumns {

	private String relSourceTable;

	private List<String> relTargetTables;

	private String formattedRelSource;

	private List<String> formattedRelTargets;
	
	
	public String getRelTargetAtIndex (Integer index) {
		return relTargetTables.get(index);
	}
	
	public String getFormattedRelTargetAtIndex (Integer index) {
		return formattedRelTargets.get(index);
	}
}
