package com.le.conversion.web;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.le.conversion.common.WebConstants;
import com.le.conversion.common.exceptions.custom.LEException;
import com.le.conversion.finisher.sql.model.JOOQQuery;
import com.le.conversion.service.ExecutorService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api")
public class AppController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private ExecutorService executorService;

	@GetMapping({ "/home", "/home/**", "/home/**/**" })
	public String index() {
		return WebConstants.FORWARD_ANG_PATH.val();
	}

	@PostMapping({ "/mapping/process" })
	public @ResponseBody JOOQQuery processFile(MultipartHttpServletRequest request) throws LEException {

		request.getParameterMap();
		Iterator<String> fileNameItr = request.getFileNames();
		MultipartFile mpMappingFile = request.getFile(fileNameItr.next());
		//File mappingFile = new File(mpMappingFile.getOriginalFilename());
		File mappingFile = null;
		try {
			mappingFile = File.createTempFile("upload", ".xls");
			mpMappingFile.transferTo(mappingFile);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			if (mappingFile != null) {
				mappingFile.delete();
			}
			throw new LEException("PARSE.ERR", e.getMessage());
		}

		return this.executorService.execute(mappingFile);

	}

	/*@PostMapping ({"/mapping/process/old"})
	public @ResponseBody JOOQQuery processFile (@RequestParam("file") MultipartFile mpMappingFile) throws LEException {
		
		CommonsMultipartFile commonsMultipartFile = (CommonsMultipartFile) mpMappingFile;
		FileItem fileItem = commonsMultipartFile.getFileItem();
	    DiskFileItem diskFileItem = (DiskFileItem) fileItem;
	    String absPath = diskFileItem.getStoreLocation().getAbsolutePath();

		File mappingFile = new File(absPath);
		try {
			if (mappingFile.exists()) {
				mappingFile.delete();
			}
			mpMappingFile.transferTo(mappingFile);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			throw new LEException("PARSE.ERR", e.getMessage());
		}
		
		return this.executorService.execute(mappingFile);
		
	}*/

	@ExceptionHandler(LEException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody String handleCustomException(LEException se) {

		String errorMsg = messageSource.getMessage(se.code(), null, Locale.US);
		log.info("MatchingController.handleException method - errorCode:" + se.code() + ", errorMsg:" + errorMsg);
		return errorMsg;

	}

}
