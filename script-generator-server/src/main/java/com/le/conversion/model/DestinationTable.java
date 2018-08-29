package com.le.conversion.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
public class DestinationTable {

	@Getter @Setter
	String name;
		
	@Getter @Setter
	List<Integer> indexes;
	
	public void addIndex(Integer index) {
		if (indexes == null) indexes = new ArrayList<>();
			
		indexes.add(index);
	}
}
