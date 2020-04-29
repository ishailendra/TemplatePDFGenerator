package com.shail.pdfgenerator.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
 
public class B64ImgReplacedElementFactory implements ReplacedElementFactory {
 
 public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight) {
     Element e = box.getElement();
     if (e == null) {
         return null;
     }
     String nodeName = e.getNodeName();
     if (nodeName.equals("img")) {
         String attribute = e.getAttribute("src");
         FSImage fsImage;
         try {
             fsImage = buildImage(attribute, uac);
         } catch (BadElementException e1) {
             fsImage = null;
         } catch (IOException e1) {
             fsImage = null;
         }
         if (fsImage != null) {
             if (cssWidth != -1 || cssHeight != -1) {
                 fsImage.scale(cssWidth, cssHeight);
             }
             return new ITextImageElement(fsImage);
         }
     }
     return null;
 }
 
 protected FSImage buildImage(String srcAttr, UserAgentCallback uac) throws IOException, BadElementException {
      FSImage fsImage;
      System.err.println("SrcAttr: "+srcAttr);
      if (srcAttr.startsWith("data:image/")) {
         String b64encoded = srcAttr.substring(srcAttr.indexOf("base64,") + "base64,".length(), srcAttr.length());
         byte[] decodedBytes = new sun.misc.BASE64Decoder().decodeBuffer(b64encoded);
         fsImage = new ITextFSImage(Image.getInstance(decodedBytes));
      } else {
         fsImage = uac.getImageResource(srcAttr).getImage();
         if(fsImage == null) {
        	 try {
        	 URL url = new URL(srcAttr);
        	 URLConnection con = url.openConnection();
        	 
        	 InputStream inputStream = con.getInputStream();
             BufferedInputStream reader = new BufferedInputStream(inputStream);
  
             BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream("resources/images/image.jpg"));
  
             byte[] buffer = new byte[4096];
             int bytesRead = -1;
  
             while ((bytesRead = reader.read(buffer)) != -1) {
                 writer.write(buffer, 0, bytesRead);
             }
  
             writer.close();
             reader.close();
             
             fsImage = uac.getImageResource("resources/images/image.jpg").getImage();
             if(fsImage != null) {
            	 System.err.println("fsImage: "+fsImage.getHeight()+"   "+fsImage.getWidth());
             }
        	 }catch(Exception e) {
        		 e.printStackTrace();
        	 }
         }
      }
      return fsImage;
 }
 
 public void remove(Element e) {
 }
 
 public void reset() {
 }
 
 @Override
 public void setFormSubmissionListener(FormSubmissionListener listener) {
 }
}