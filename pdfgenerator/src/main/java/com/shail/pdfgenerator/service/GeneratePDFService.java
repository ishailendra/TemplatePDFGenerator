package com.shail.pdfgenerator.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;

import com.shail.pdfgenerator.utils.FreemarkerUtil;

@Service
public class GeneratePDFService {

	@Autowired
	FreemarkerUtil freemarkerUtil;
	public void buildPDfDoc() throws IOException {
		System.out.println("generating pdf...");
		String outfilepath = "E:\\LocalProjects\\JavaProjects\\pdfgenerator\\src\\main\\resources\\outputs\\generateddoc.pdf";
		Map<String, Object> dataModel = new HashMap<>();
		dataModel.put("mobile", "1234567890");
		dataModel.put("name", "Shail");
		dataModel.put("email", "shail@mail.com");
		URL fileResource = GeneratePDFService.class.getResource("/templates");
		
		Path path = Paths.get("E:\\LocalProjects\\JavaProjects\\Screenshot.png");
		String image = convertToBase64(path);
		image = "data:image/png;base64, "+ image;
		dataModel.put("image",image);
		String html = freemarkerUtil.loadFtlHtml(new File(fileResource.getFile()), "simpleForm.ftl", dataModel);
		System.err.println("HTML: "+html);
		System.out.println("converting to xml");
		Document doc = XMLResource.load(new ByteArrayInputStream(html.getBytes())).getDocument();
		System.out.println("converted to xml");
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocument(doc, null);
		renderer.layout();
		System.out.println("generating pdf");
		try (OutputStream os = Files.newOutputStream(Paths.get(outfilepath))) {
		    renderer.createPDF(os);
		}
		System.out.println("gen done");
	}
	
	private String convertToBase64(Path path) {
	    byte[] imageAsBytes = new byte[0];
	    try {
	      Resource resource = new UrlResource(path.toUri());
	      InputStream inputStream = resource.getInputStream();
	      imageAsBytes = IOUtils.toByteArray(inputStream);

	    } catch (IOException e) {
	      System.out.println("\n File read Exception");
	    }

	    return Base64.getEncoder().encodeToString(imageAsBytes);
	  }
}
