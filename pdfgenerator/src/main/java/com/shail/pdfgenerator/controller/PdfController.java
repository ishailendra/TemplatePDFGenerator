package com.shail.pdfgenerator.controller;

import java.io.File;
import java.io.FileOutputStream;
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
	
	@GetMapping("/createWithImage")
	public ResponseEntity<byte[]> pdfController() throws Exception {
		
			byte[] bytes = genPdfService.buildPDfDoc();
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
			
	}
	
	@GetMapping("/createWithoutImage")
	public ResponseEntity<byte[]> printPdf() throws Exception {
	
		String baseUrl = context.getRealPath("/");
		String templateToBeUsed = "sampleTemplate.ftl";
		Map<String, Object> dataModel = new HashMap<>();
		dataModel.put("title", "Lorem Ipsum");
		dataModel.put("p2", "Aliquam at nisi vitae");
		dataModel.put("p3", "Integer in purus");
		dataModel.put("p4", "Quisque tempus tellus in lorem");
		
		byte[] bytes = genPdfService.createPdf(baseUrl, templateToBeUsed, dataModel).toByteArray();
		FileOutputStream fos = new FileOutputStream(new File("src/main/resources/output/genpdf.pdf"));
		fos.write(bytes);
		fos.close();
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
		
}
}
