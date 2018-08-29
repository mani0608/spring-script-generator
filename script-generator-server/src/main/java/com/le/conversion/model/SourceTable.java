package com.le.conversion.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
public class SourceTable {

	@Getter @Setter
	String name;

	@Getter @Setter
	List<Integer> indexes;

	@Getter @Setter
	List<DestinationTable> destTableIndexes;

	public void addIndex(Integer index) {
		if (indexes == null)
			indexes = new ArrayList<>();

		indexes.add(index);
	}

	public void addDestTableIndex(DestinationTable destTableIndex) {

		if (this.destTableIndexes == null)
			this.destTableIndexes = new ArrayList<>();

		this.destTableIndexes.add(destTableIndex);

	}

	public List<DestinationTable> getSortedDestTableIndexes() {
		return this.destTableIndexes.stream().sorted(Comparator.comparing(DestinationTable::getName)).collect(Collectors.toList());
	}

}
