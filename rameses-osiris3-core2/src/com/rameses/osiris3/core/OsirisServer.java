/*
 * OsirisServer.java
 *
 * Created on January 26, 2013, 7:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class OsirisServer {
    
    public final static String APP_URLS_PROPERTY = "app.urls";
    
    private static OsirisServer instance;
    
    public static OsirisServer getInstance() {
        return instance;
    }

    public static void setInstance(OsirisServer  s) {
        instance = s;
    }
    
    /**************************************************************************
     * App Root
     *************************************************************************/
    public abstract void init() throws Exception;
    
    private String rootUrl;
    private Map<Class, ContextProvider> contextProviders = Collections.synchronizedMap( new HashMap() );
    private Map<String, ServerResource> resources = Collections.synchronizedMap( new HashMap() );
    protected Map conf;
    
    public OsirisServer(String s, Map conf) {
        if(s.endsWith("/")) s = s.substring(0, s.length()-1);
        this.rootUrl = s;
        this.conf = conf;
    }
    
    protected final void addContextProvider(Class c, ContextProvider p) {
        contextProviders.put( c, p );
    }
    
    public final <T> T getContext(Class<T> c, String name) {
        if(name==null) return null;
        if( !contextProviders.containsKey(c)) 
            throw new RuntimeException("Context provider for " + c + " not installed");
        return (T) contextProviders.get( c ).getContext( name );
    }

    protected final void addResource(Class c, ServerResource res) {
        resources.put( c.getSimpleName(), res );
    }
    
    public final <T> T getResource(Class<T> c) {
        if( !resources.containsKey(c.getSimpleName())) 
            throw new RuntimeException("Service " + c.getSimpleName() + " not installed");
        return (T) resources.get(c.getSimpleName());
    }

    public final ServerResource getResource(String s) {
        if( !resources.containsKey(s) ) 
            throw new RuntimeException("Service " + s + " not installed");
        return (ServerResource) resources.get(s);
    }

    public final String getRootUrl() {
        return rootUrl;
    }
    
    public String getCluster() {
        return (String)conf.get("cluster");
    }
    
    
    public void start() {
        System.out.println("starting server context root @ " + rootUrl );
        try {
            init();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stop() 
    {
        for (ContextProvider cp : this.contextProviders.values()) {
            cp.stop();
        }
        
        for (ServerResource res : this.resources.values()) {
            res.destroy(); 
        }
    }
}
