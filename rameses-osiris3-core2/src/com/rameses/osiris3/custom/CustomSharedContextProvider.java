/*
 * CustomSharedContextProvider.java
 *
 * Created on January 27, 2013, 5:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.custom;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.ContextProvider;
import com.rameses.osiris3.core.ContextResource;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.core.SharedContext;
import com.rameses.util.Service;
import java.util.Iterator;

/**
 *
 * @author Elmo
 */
public class CustomSharedContextProvider extends ContextProvider {
    
    public CustomSharedContextProvider(OsirisServer s) {
        super(s);
    }
    
    protected AbstractContext findContext(String name) {
        return new SharedContext(server, name);
    }
    
    protected void initContext(AbstractContext ac) {
        Iterator<ContextResource> res = Service.providers(ContextResource.class, getClass().getClassLoader());
        while(res.hasNext()) {
            ContextResource cs = res.next();
            cs.setServer(server);
            cs.setContext(ac);
            cs.init();
            ac.addResource( cs.getResourceClass(), cs );
        }
    }
    
    
    public String getRootUrl() {
        return server.getRootUrl()+"/shared";
    }
    
    protected String getConfUrl(String name) {
        return null;
    }
    protected String getClassLoaderPath(String name) {
        return getRootUrl()+ "/" + name +"/modules";
    }

    
}
