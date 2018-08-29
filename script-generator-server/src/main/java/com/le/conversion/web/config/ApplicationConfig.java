package com.le.conversion.web.config;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.le.conversion.common.util.CacheManagerUtil;
import com.le.conversion.finisher.FinisherManager;
import com.le.conversion.loader.LoaderManager;
import com.le.conversion.model.ExecutionData;
import com.le.conversion.processor.ProcessorManager;
import com.le.conversion.processor.sql.SQLProcessor;
import com.le.conversion.processor.sql.SQLScriptManager;

import lombok.extern.log4j.Log4j2;

@Log4j2
@EnableCaching
@Configuration
@ComponentScan({"com.le.conversion.web", "com.le.conversion.service", "com.le.conversion.model", "com.le.conversion.loader",
		"com.le.conversion.processor", "com.le.conversion.finisher" })
public class ApplicationConfig implements WebMvcConfigurer {
	
	@Autowired
	Environment environment;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:8050", "http://localhost:4200", "http://localhost:8080", "http://10.104.94.110:8080")
			.allowedMethods("POST", "PUT", "GET", "OPTIONS", "DELETE")
			.allowedHeaders("x-requested-with", "X-Auth-Token", "Content-Type", "Accept", "Content-Disposition", "enctype")
			.allowCredentials(true).maxAge(3600);
	}
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		ObjectMapper mapper = builder.build();
		mapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		converters.add(new MappingJackson2HttpMessageConverter(mapper));
		converters.add(new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build()));
	}

	@Bean
	public ExecutionData executionData() {
		return new ExecutionData();
	}

	@Bean
	public LoaderManager loaderManager() {
		return new LoaderManager();
	}

	@Bean
	public ProcessorManager processorManager() {
		return new ProcessorManager();
	}

	@Bean
	public FinisherManager finisherManager() {
		return new FinisherManager();
	}

	@Bean
	public SQLScriptManager sqlScriptManager() {
		return new SQLScriptManager();
	}

	@Bean
	public SQLProcessor sqlProcessor() {
		return new SQLProcessor();
	}

	@Bean
	public CacheManagerUtil cacheManagerUtil() {
		return new CacheManagerUtil();
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("locale/messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	public CacheManager concurrentMapCacheManager() {
		log.info("Loading cache manager in AppConfig");
		log.info("Environment is Dev, so CaheManager is ConcurrentMapCacheManager");
		ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
		String[] arrCacheNames = new String[] { "generatorCache" };
		cacheManager.setCacheNames(Arrays.asList(arrCacheNames));
		log.info("ConcurrentMapCacheManager cache list:" + Arrays.asList(cacheManager.getCacheNames()));
		return cacheManager;
	}
	
	@Bean 
	@Order(0) 
	public MultipartFilter multipartFilter(){ 
		MultipartFilter multipartFilter = new MultipartFilter(); 
		multipartFilter.setMultipartResolverBeanName("multipartResolver"); 
		return multipartFilter; 
	}
	
	@Bean
	public CommonsMultipartResolver multipartResolver(){
	    CommonsMultipartResolver resolver = new CommonsMultipartResolver();
	    resolver.setMaxUploadSize(5242880); // set the size limit
	    return resolver;
	}
	
	/*@Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }
*/
}
