package com.le.conversion.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class Criterian {

	@Getter
	private List<Criteria> criterias;
	
	public void addCriteria(Criteria criteria) {
		
		if (this.criterias == null) this.criterias = new ArrayList<>();
		
		this.criterias.add(criteria);
		
	}
	
}
