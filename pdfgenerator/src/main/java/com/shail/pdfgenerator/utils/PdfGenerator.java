package com.shail.pdfgenerator.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class PdfGenerator {

	private static Logger logger = LoggerFactory.getLogger(PdfGenerator.class);
	
	public ByteArrayOutputStream createPdf(Map<String, Object> dataMapForTemplate, String templateToBeUsed, String baseUrl) {
		
		Configuration cfg = null;
		Map<String, Object> data = null;
		Writer out = null;
		Template template = null;
		ByteArrayOutputStream baos = null;
		
		data = new HashMap<String, Object>();
		data.putAll(dataMapForTemplate);
		
		try {
			cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
			cfg.setClassForTemplateLoading(this.getClass(), "/");
			
			logger.debug("Loading Template: ", templateToBeUsed);
			logger.debug("Template path: ","templates/"+templateToBeUsed);
			
			template = cfg.getTemplate("templates/"+templateToBeUsed);
			
			out = new StringWriter();
			logger.debug("Setting data in template");
			template.process(data, out);
			logger.debug("template processed");
			
			String htmlString = (out.toString()).replaceAll(" & "," &#38; ");
			logger.debug("Html Generated: \n"+htmlString);
			
			baos = new ByteArrayOutputStream();
			
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(htmlString, "file:///"+baseUrl);
			renderer.layout();
			renderer.createPDF(baos);
			logger.debug("Pdf created as ByteArrayOutputStream");
			return baos;
			
		}
		catch(Exception e) {
			logger.error("Some exception occured while creating pdf doc", e);
		}
		finally {
			try {
				baos.close();
				if(out!=null) {
					out.close();
				}
			} catch (IOException e) {
				logger.error("Some error occurred while closing baos and stringWriter");
			}
		}
		
		return baos;
	}
}
