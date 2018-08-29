package com.le.conversion.finisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.le.conversion.common.Messages;
import com.le.conversion.common.exceptions.custom.LoaderException;
import com.le.conversion.finisher.sql.JOOQProcessor;
import com.le.conversion.finisher.sql.model.JOOQQuery;
import com.le.conversion.model.ExecutionData;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class FinisherManager {
	
	@Autowired
	private ExecutionData executionData;

	public JOOQQuery finish() throws LoaderException {
		
		log.debug(Messages.FINISHER_START);
		
		executionData.initializeFinisher();
		
		JOOQProcessor processor = JOOQProcessor.getInstance(executionData.creator());
		
		executionData.queries().stream().forEach(query -> {
			executionData.jooqQuery().getSourceTableQueries().add(processor.generateScript(query));
		});
		
		log.debug(Messages.FINISHER_COMPLETE);
		
		return executionData.jooqQuery();
	}

}
