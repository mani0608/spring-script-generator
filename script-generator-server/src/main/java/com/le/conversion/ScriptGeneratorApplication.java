package com.le.conversion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableAutoConfiguration(exclude=MultipartAutoConfiguration.class)
public class ScriptGeneratorApplication  extends SpringBootServletInitializer {
	
	public static void main(String[] args) {
		SpringApplication.run(ScriptGeneratorApplication.class, args);
	}
}
