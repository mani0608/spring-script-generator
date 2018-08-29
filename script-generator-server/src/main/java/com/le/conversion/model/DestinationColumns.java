package com.le.conversion.model;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

@ToString
public class DestinationColumns<T> {

	List<T> columns;
	
	public DestinationColumns() {
		this.columns = new ArrayList<>();
	}
	
	public DestinationColumns(List<T> columns) {
		this.columns = columns;
	}
	
	public List<T> getColumns() {
		return this.columns;
	}
	
	public Integer getColumnsCount() {
		return this.columns.size();
	}
	
	public void add(T column) {
		this.columns.add(column);
	}
	
	public T get(Integer index) {
		return this.columns.get(index);
	}
}
