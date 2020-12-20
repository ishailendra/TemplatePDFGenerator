package com.shail.pdfgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@SpringBootApplication
public class PdfgeneratorApplication extends WebMvcConfigurationSupport {

	public static void main(String[] args) {
		SpringApplication.run(PdfgeneratorApplication.class, args);
	}

	@Override 
	protected void configurePathMatch(PathMatchConfigurer configurer) { 
		 AntPathMatcher matcher = new AntPathMatcher();
	     matcher.setCaseSensitive(false);
	     configurer.setPathMatcher(matcher);
		}
}
