package com.shail.pdfgenerator.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;

import com.shail.pdfgenerator.utils.B64ImgReplacedElementFactory;
import com.shail.pdfgenerator.utils.FreemarkerUtil;

@Service
public class GeneratePDFService {

	@Autowired
	FreemarkerUtil freemarkerUtil;
	public byte[] buildPDfDoc() throws IOException {
		System.out.println("generating pdf...");

		
//		String outfilepath = "E:\\LocalProjects\\JavaProjects\\pdfgenerator\\src\\main\\resources\\outputs\\generateddoc.pdf";
		
		Path path = Paths.get("E:\\LocalProjects\\JavaProjects\\star.jpg");
		String image = convertToBase64(path);
		image = "data:image/jpeg;base64,"+ image;
		
		Map<String, Object> dataModel = new HashMap<>();
		dataModel.put("mobile", "1234567890");
		dataModel.put("name", "Shail");
		dataModel.put("email", "shail@mail.com");
		dataModel.put("image",image);
		
		
		URL fileResource = GeneratePDFService.class.getResource("/templates");
		String html = freemarkerUtil.loadFtlHtml(new File(fileResource.getFile()), "simpleForm.ftl", dataModel);
		
		System.err.println("HTML: "+html);
		
		System.out.println("converting to xml");
		Document doc = XMLResource.load(new ByteArrayInputStream(html.getBytes())).getDocument();
		System.out.println("converted to xml");
		
		ITextRenderer renderer = new ITextRenderer();
		
		SharedContext sharedContext = renderer.getSharedContext();
		sharedContext.setPrint(true);
		sharedContext.setInteractive(false);
		sharedContext.setReplacedElementFactory(new B64ImgReplacedElementFactory());
		sharedContext.getTextRenderer().setSmoothingThreshold(0);

		renderer.setDocument(doc,null);
		renderer.layout();
		
		System.out.println("generating pdf");
		byte[] bytes = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
		    renderer.createPDF(baos);
		    bytes = baos.toByteArray();
		    
		   FileOutputStream fos = new FileOutputStream(new File("E:\\LocalProjects\\JavaProjects\\pdfgenerator\\src\\main\\resources\\outputs\\generateddoc2.pdf"));
		   fos.write(bytes);
		   fos.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("gen done");
		return bytes;
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
