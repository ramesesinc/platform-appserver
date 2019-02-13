/*
 * CustomPluginContextProvider.java
 *
 * Created on January 27, 2013, 5:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.custom;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.PluginContext;
import com.rameses.osiris3.core.OsirisServer;

/**
 *
 * @author Elmo
 */
public class CustomPluginContextProvider extends CustomAppContextProvider {
    
    public CustomPluginContextProvider(OsirisServer svr) {
        super(svr );
    }
    
    public AbstractContext findContext(String name) {
        return new PluginContext( server, name);
    }
    
    public String getRootUrl() {
        return server.getRootUrl()+"/plugins";
    }

    protected String getConfUrl(AbstractContext c) {
        return c.getRootUrl() + "/app.conf";
    }
    
    protected String getClassLoaderPath(AbstractContext c) {
        return c.getRootUrl() + "/modules";
    }

    
}
