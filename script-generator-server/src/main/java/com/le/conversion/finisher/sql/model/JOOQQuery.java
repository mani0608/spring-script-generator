package com.le.conversion.finisher.sql.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JOOQQuery {
	
	List<JOOQSourceTableQuery> sourceTableQueries;
	
	public void initialize() {
		sourceTableQueries = new ArrayList<>();
	}
	
}
