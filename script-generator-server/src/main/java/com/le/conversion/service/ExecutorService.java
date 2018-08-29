package com.le.conversion.service;

import java.io.File;

import com.le.conversion.finisher.sql.model.JOOQQuery;

public interface ExecutorService {
	
	public JOOQQuery execute(File mappingFile);

}
