/*
 * URLRuleResource.java
 *
 * Created on February 12, 2013, 10:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.rules;

import com.rameses.util.URLDirectory;
import com.rameses.util.URLDirectory.URLFilter;
import java.io.InputStream;
import java.net.URL;

/**
 *
 * @author Elmo
 */
public abstract class URLRuleResource implements RuleResource {
    
    protected RuleContext ruleContext;
    
    public void setRuleContext(RuleContext c) {
        this.ruleContext = c;
    }
    
    protected void load(URL dir, ClassLoader loader, final RuleResource.Handler handler) throws Exception{
        URLDirectory udir = new URLDirectory(dir);
        udir.list( new URLFilter(){
            public boolean accept(URL u, String filter) {
                if(filter.endsWith(".drl")) {
                    InputStream is = null;
                    try {
                        is = u.openStream();
                        handler.handle( is );
                    } catch(Exception e) {
                        System.out.println("ERROR loading " + u.getFile() + ". " +e.getMessage());
                    } finally {
                        try {is.close();} catch(Exception ign){;}
                    }
                }
                return false;
            }
        }, loader);
    }
    
    
}
