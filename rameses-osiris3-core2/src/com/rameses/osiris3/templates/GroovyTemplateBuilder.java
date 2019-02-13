/*
 * GroovyTemplateBuilder.java
 *
 * Created on February 7, 2013, 8:41 PM
 *
 * To change this template, choose Tools | GroovyTemplateBuilder Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.templates;

import groovy.text.SimpleTemplateEngine;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class GroovyTemplateBuilder implements TemplateBuilder {
    
    public String getExtension() {
        return "gtpl";
    }
    
    public Template build(InputStream is) throws Exception {
        SimpleTemplateEngine st = new SimpleTemplateEngine();
        InputStreamReader rd = new InputStreamReader(is);
        final groovy.text.Template gt = st.createTemplate(rd);
        return new Template() {
            public Object render(Object data) {
                if(!(data instanceof Map))
                    throw new RuntimeException("Data must be an instance of Map");
                Map map = (Map)data;
                return gt.make( map );
            }
        };
    }
    
}
