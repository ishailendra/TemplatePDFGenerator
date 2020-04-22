package com.shail.pdfgenerator.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shail.pdfgenerator.service.GeneratePDFService;

@RestController
public class PdfController {

	@Autowired
	GeneratePDFService genPdfService;
	@GetMapping("/generate")
	public String pdfController() {
		try {
			genPdfService.buildPDfDoc();
			return "done";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
	}
}
