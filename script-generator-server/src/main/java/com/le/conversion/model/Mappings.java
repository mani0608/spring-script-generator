package com.le.conversion.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Builder
@Log4j2
public class Mappings {

	@Getter
	@Setter
	List<SourceTableMapping> mappingData;

	public void addMapping(SourceTableMapping mapping) {
		if (mappingData == null)
			mappingData = new ArrayList<>();

		mappingData.add(mapping);
	}

	public void printLog() {
		log.debug("Total Source tables: " + mappingData.size());

		log.debug("***********Total destination tables for each source table***********");

		mappingData.forEach(stm -> log.debug("Source Table: " + stm.getSourceTable() + " Destination Table Count: "
				+ stm.getDestTableMappings().size()));

		log.debug("***********Mapping count for each destination table***********");

		mappingData.forEach(stm -> {
			stm.getDestTableMappings().forEach(dtm -> {
				log.debug("Source Table: " + stm.getSourceTable() + " Destination Table: " + dtm.getDestinationTable()
						+ " Mapping count: " + dtm.getMappingProps().size());
			});
		});
		
		log.debug("***********Total Destination Mapping count***********");
		
		mappingData.forEach(stm -> {
			Integer count = stm.getDestTableMappings().stream().collect(Collectors.summingInt(p -> p.getMappingProps().size()));
			log.debug("Total Destination Mapping count for source table " + stm.getSourceTable() + " is: " + count);
		});
	}

}
