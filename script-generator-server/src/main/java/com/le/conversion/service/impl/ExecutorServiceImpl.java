package com.le.conversion.service.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.le.conversion.common.exceptions.ExceptionWrapper;
import com.le.conversion.finisher.FinisherManager;
import com.le.conversion.finisher.sql.model.JOOQQuery;
import com.le.conversion.loader.LoaderManager;
import com.le.conversion.processor.ProcessorManager;
import com.le.conversion.service.ExecutorService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ExecutorServiceImpl implements ExecutorService {

	@Autowired
	private LoaderManager loader;

	@Autowired
	private ProcessorManager processor;

	@Autowired
	private FinisherManager finisher;

	public JOOQQuery execute(File mappingFile) {

		// STEP #1: LOADER -> processor -> finisher

		log.debug("---------Bootstrapping loader---------");
		ExceptionWrapper.handleSupplier(() -> loader.load(mappingFile), String.class).get();

		// STEP #2: loader -> PROCESSOR -> finisher

		log.debug("---------Bootstrapping processor---------");
		ExceptionWrapper.handleSupplier(() -> processor.process(), String.class).get();

		// STEP #3: loader -> processor -> FINISHER

		log.debug("---------Bootstrapping finisher---------");
		return ExceptionWrapper.handleSupplier(() -> finisher.finish(), JOOQQuery.class).get();

	}
}
