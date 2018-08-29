package com.le.conversion.loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.StampedLock;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.le.conversion.common.CacheConstants;
import com.le.conversion.common.Messages;
import com.le.conversion.common.exceptions.custom.LoaderException;
import com.le.conversion.common.util.CacheManagerUtil;
import com.le.conversion.loader.util.LoaderUtil;
import com.le.conversion.model.ExecutionData;

import lombok.extern.log4j.Log4j2;

/**
 * This is a single-ton entry point for loader This class will get the mapping
 * file, reads the mapping and then calls the processor
 * 
 * @author Manikandan.R
 *
 */

@Log4j2
@Component
public class LoaderManager {

	@Autowired
	private ExecutionData executionData;

	@Autowired
	private CacheManagerUtil cacheUtil;

	private String cacheName;

	private StampedLock lock = new StampedLock();

	public String load(String filePath) throws LoaderException {

		// Init Cache properties
		this.cacheName = CacheConstants.GENERATOR_CACHE.val();

		// TODO: Code for loader

		log.debug(Messages.LOADER_START.message());

		// getFilePathFromUser();

		executionData.mappingFilePath(filePath);

		log.debug(Messages.MAPPING_PATH.message() + ": " + filePath);

		long stamp = lock.writeLock();

		try {

			Path fp = Paths.get(filePath);

			if (Files.exists(fp, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {

				try {
					executionData.mappings(LoaderUtil.readFile(filePath));
				} catch (InvalidFormatException | IOException e) {
					// TODO Auto-generated catch block
					throw new LoaderException(e.getMessage());
				}

				addResultToCache(executionData);

			}

		} finally {
			lock.unlockWrite(stamp);
		}

		log.debug(Messages.LOADER_COMPLETE.message());

		log.debug(Messages.LOADER_SUCCESS.message());

		return Messages.LOADER_SUCCESS.message();
	}

	public String load(File mappingFile) throws LoaderException {

		// Init Cache properties
		this.cacheName = CacheConstants.GENERATOR_CACHE.val();

		try {
			executionData.mappings(LoaderUtil.readFile(mappingFile));
		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			throw new LoaderException(e.getMessage());
		}

		addResultToCache(executionData);

		if (mappingFile != null) {
			mappingFile.delete();
		}

		log.debug(Messages.LOADER_COMPLETE.message());

		log.debug(Messages.LOADER_SUCCESS.message());

		return Messages.LOADER_SUCCESS.message();

	}

	private void addResultToCache(ExecutionData executionData) {

		cacheUtil.addToCache(this.cacheName, CacheConstants.EXECUTION_DATA.val(), executionData);
	}
}
