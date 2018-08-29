package com.le.conversion.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Criteria {

	private String criteriaParam;
	
	private String criteriaValue;
	
}
