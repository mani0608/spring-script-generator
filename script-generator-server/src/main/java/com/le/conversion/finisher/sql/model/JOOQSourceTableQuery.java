package com.le.conversion.finisher.sql.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JOOQSourceTableQuery {

	List<String> importQueries;
	
	public void initialize() {
		importQueries = new ArrayList<>();
	}
	
}
