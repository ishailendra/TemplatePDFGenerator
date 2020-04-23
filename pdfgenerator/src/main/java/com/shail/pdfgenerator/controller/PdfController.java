package com.shail.pdfgenerator.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shail.pdfgenerator.service.PdfService;

@RestController
public class PdfController {

	@Autowired
	PdfService genPdfService;
	
	@Autowired
	ServletContext context;
	
	@GetMapping("/generate")
	public ResponseEntity<byte[]> pdfController() throws Exception {
		
			byte[] bytes = genPdfService.buildPDfDoc();
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
			
	}
	
	@GetMapping("/create")
	public ResponseEntity<byte[]> printPdf() throws Exception {
	
		String baseUrl = context.getRealPath("/");
		String templateToBeUsed = "sampleTemplate.ftl";
		
		Map<String, Object> dataModel = new HashMap<>();
		dataModel.put("mobile", "1234567890");
		dataModel.put("name", "Shail");
		dataModel.put("email", "shail@mail.com");
		
		byte[] bytes = genPdfService.createPdf(baseUrl, templateToBeUsed, dataModel).toByteArray();
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
		
}
}
