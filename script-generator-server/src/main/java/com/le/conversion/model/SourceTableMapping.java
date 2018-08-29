package com.le.conversion.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.le.conversion.common.exceptions.custom.CommonException;
import com.le.conversion.common.util.CommonUtil;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SourceTableMapping {

	String sourceTable;

	String primaryKeyCol;

	List<DestinationTableMapping> destTableMappings;

	public void addDestTableMappings(DestinationTableMapping destTableMapping) throws CommonException {
		if (destTableMappings == null)
			destTableMappings = new ArrayList<>();

		// If mapping for table already exists then append the new mapping
		if (CommonUtil.contains(destTableMappings, "destinationTable", destTableMapping.getDestinationTable())) {

			DestinationTableMapping mapping = destTableMappings.stream().filter(dtMapping -> StringUtils
					.equals(dtMapping.getDestinationTable(), destTableMapping.getDestinationTable())).findFirst().get();

			mapping.getMappingProps().addAll(destTableMapping.getMappingProps());

		} else {
			destTableMappings.add(destTableMapping);
		}
	}
}
