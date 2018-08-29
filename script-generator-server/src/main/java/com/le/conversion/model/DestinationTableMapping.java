package com.le.conversion.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DestinationTableMapping {
	
	private String destinationTable;
	
	List<MappingProperties> mappingProps;
	
}
