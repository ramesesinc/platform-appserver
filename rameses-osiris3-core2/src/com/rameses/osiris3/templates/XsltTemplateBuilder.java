/*
 * GroovyTemplateBuilder.java
 *
 * Created on February 7, 2013, 8:41 PM
 *
 * To change this template, choose Tools | GroovyTemplateBuilder Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.templates;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author Elmo
 */
public class XsltTemplateBuilder implements TemplateBuilder {
    
    public String getExtension() {
        return "xslt";
    }
    
    public Template build(InputStream is) throws Exception {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        StreamSource source = new StreamSource(is);
        Transformer transformer = tFactory.newTransformer(source);
        return new XsltRenderer(transformer);
    }
    
    private class XsltRenderer implements Template {
        private Transformer transformer;
        
        public XsltRenderer(Transformer t) {
            this.transformer = t ;
        }
        
        public Object render(Object data) {
            StringWriter swriter = new StringWriter();
            transform(data, swriter);
            return swriter.toString();
        }
        
        public void transform(Object data, Writer out ) {
            transform( data, new StreamResult(out) );            
        }
        
        public void transform(Object data, OutputStream out ) {
            transform( data, new StreamResult(out) );
        }
        
        public void transform(Object data, StreamResult sresult ) {
            try {
                if(!(data  instanceof String))
                    throw new Exception("Source data must be an xml document");
                ByteArrayInputStream bis = new ByteArrayInputStream(data.toString().getBytes());
                StreamSource sourceData = new StreamSource(bis);
                transformer.transform(sourceData, sresult);
            } catch(RuntimeException re) {
                throw re;
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
    
}
