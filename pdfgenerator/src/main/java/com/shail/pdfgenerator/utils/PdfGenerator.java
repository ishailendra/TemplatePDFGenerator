package com.shail.pdfgenerator.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import com.lowagie.text.Image;

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
			
			URL url = new URL("https://earthsky.org/upl/2020/01/Orion-south-evening.jpg");
			URLConnection con = url.openConnection();
			InputStream inputStream = con.getInputStream();
			BufferedInputStream reader = new BufferedInputStream(inputStream);
			
			BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream("file.jpg"));
			
			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			
			while ((bytesRead = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, bytesRead);
			}
			
			writer.close();
			reader.close();
			
			String htmlString = (out.toString()).replaceAll(" & "," &#38; ");
			logger.debug("Html Generated: \n"+htmlString);
			System.err.println(htmlString);
			baos = new ByteArrayOutputStream();
			
			ITextRenderer renderer = new ITextRenderer();
			SharedContext sharedContext = renderer.getSharedContext();
			sharedContext.setPrint(true);
			sharedContext.setInteractive(false);
			sharedContext.setReplacedElementFactory(new B64ImgReplacedElementFactory());
			sharedContext.getTextRenderer().setSmoothingThreshold(0);

			renderer.setDocumentFromString(htmlString);
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



/**
 * Replaced element in order to replace elements like 
 * <tt>&lt;div class="media" data-src="image.png" /></tt> with the real
 * media content.
 */
class MediaReplacedElementFactory implements ReplacedElementFactory {
    private final ReplacedElementFactory superFactory;

    public MediaReplacedElementFactory(ReplacedElementFactory superFactory) {
        this.superFactory = superFactory;
    }

    @Override
    public ReplacedElement createReplacedElement(LayoutContext layoutContext, BlockBox blockBox, UserAgentCallback userAgentCallback, int cssWidth, int cssHeight) {
        Element element = blockBox.getElement();
        if (element == null) {
            return null;
        }
        String nodeName = element.getNodeName();
        String className = element.getAttribute("class");
        // Replace any <div class="media" data-src="image.png" /> with the
        // binary data of `image.png` into the PDF.
        if ("div".equals(nodeName) && "media".equals(className)) {
            if (!element.hasAttribute("data-src")) {
                throw new RuntimeException("An element with class `media` is missing a `data-src` attribute indicating the media file.");
            }
            InputStream input = null;
            try {
                input = new FileInputStream("/base/folder/" + element.getAttribute("data-src"));
                final byte[] bytes = IOUtils.toByteArray(input);
                final Image image = Image.getInstance(bytes);
                final FSImage fsImage = new ITextFSImage(image);
                if (fsImage != null) {
                    if ((cssWidth != -1) || (cssHeight != -1)) {
                        fsImage.scale(cssWidth, cssHeight);
                    }
                    return new ITextImageElement(fsImage);
                }
            } catch (Exception e) {
                throw new RuntimeException("There was a problem trying to read a template embedded graphic.", e);
            } finally {
                IOUtils.closeQuietly(input);
            }
        }
        return this.superFactory.createReplacedElement(layoutContext, blockBox, userAgentCallback, cssWidth, cssHeight);
    }

    @Override
    public void reset() {
        this.superFactory.reset();
    }

    @Override
    public void remove(Element e) {
        this.superFactory.remove(e);
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener listener) {
        this.superFactory.setFormSubmissionListener(listener);
    }
}
