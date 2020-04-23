package com.shail.pdfgenerator.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shail.pdfgenerator.service.GeneratePDFService;

@RestController
public class PdfController {

	@Autowired
	GeneratePDFService genPdfService;
	@GetMapping("/generate")
	public ResponseEntity<byte[]> pdfController() throws Exception {
		
			byte[] bytes = genPdfService.buildPDfDoc();
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
			
	}
}
