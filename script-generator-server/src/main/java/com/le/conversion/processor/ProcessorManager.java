package com.le.conversion.processor;

import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.le.conversion.common.CacheConstants;
import com.le.conversion.common.Messages;
import com.le.conversion.common.exceptions.custom.LoaderException;
import com.le.conversion.common.util.CacheManagerUtil;
import com.le.conversion.model.ExecutionData;
import com.le.conversion.model.SourceTableMapping;
import com.le.conversion.processor.sql.SQLScriptManager;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ProcessorManager {

	@Autowired
	private ExecutionData executionData;

	@Autowired
	private CacheManagerUtil cacheUtil;
	
	@Autowired
	private SQLScriptManager sqlScriptManager;

	private String cacheName;

	public String process() throws LoaderException {

		// Init Cache properties
		this.cacheName = CacheConstants.GENERATOR_CACHE.val();
		
		this.getDataFromCache();
		
		executionData.initializeProcessor();
		
		sqlScriptManager.executionData(executionData);

		log.debug(Messages.PROCESSOR_START);

		IntStream.range(0, this.executionData.mappings().getMappingData().size()).forEach(idx -> {
			SourceTableMapping mapping = this.executionData.mappings().getMappingData().get(idx);
			sqlScriptManager.createSQL(mapping, idx);
		});
		
		updateDataInCache();

		log.debug(Messages.PROCESSOR_COMPLETE);

		log.debug(Messages.PROCESSOR_SUCCESS);

		return Messages.PROCESSOR_SUCCESS.message();
	}

	private void getDataFromCache() {

		this.executionData = (ExecutionData) cacheUtil.getFromCache(this.cacheName, CacheConstants.EXECUTION_DATA.val());
	}
	
	private void updateDataInCache() {
		cacheUtil.addToCache(this.cacheName, CacheConstants.EXECUTION_DATA.val(), this.executionData);
	}
}
