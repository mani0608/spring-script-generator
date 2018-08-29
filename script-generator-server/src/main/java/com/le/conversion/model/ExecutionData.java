package com.le.conversion.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import com.le.conversion.finisher.sql.model.JOOQQuery;
import com.le.conversion.model.Mappings;
import com.le.conversion.processor.sql.model.SourceTableQuery;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Component
@Accessors(fluent = true)
@ToString
public class ExecutionData {

	@Getter
	@Setter
	protected Mappings mappings;

	@Getter
	@Setter
	protected String mappingFilePath;

	@Getter
	@Setter
	protected Integer queryIndex;

	@Getter
	@Setter
	protected Integer importQueryIndex;

	@Getter
	@Setter
	protected Integer importFieldIndex;

	@Getter
	protected List<SourceTableQuery> queries;
	
	@Getter
	protected JOOQQuery jooqQuery;

	@Getter
	@Setter
	protected DSLContext creator;

	public void initializeProcessor() {
		if (queries == null) queries = new ArrayList<>();
	}

	public void initializeFinisher() {

		Connection creatorConn = null;

		creator = DSL.using(creatorConn, SQLDialect.SQLITE);
	
		jooqQuery = JOOQQuery.builder().build();
		
		jooqQuery.initialize();
	}

	public static void printProcessorLog() {
	}

	public void printLoaderLog() {
		mappings.printLog();
	}

	public void printApplicationLog() {
		// printLog();
	}

}
