/*
 * GroovyTemplateBuilder.java
 *
 * Created on February 7, 2013, 8:41 PM
 *
 * To change this template, choose Tools | GroovyTemplateBuilder Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.templates;

import com.rameses.osiris3.core.ContextService;

/**
 *
 * @author Elmo
 */
public class TemplateService extends ContextService  {
    
    public Class getProviderClass() {
        return TemplateService.class;
    }
    
    public final int getRunLevel() {
        return 0;
    }
    
    public Template get(String name) {
        return context.getResource(Template.class, name);
    }

    public Object render(String name, Object data) {
        Template t= context.getResource(Template.class, name);
        return t.render(data);
    }

    public void start() throws Exception {
    }

    public void stop() throws Exception {
    }

    
    
}
