/*
 * CustomOsirisServer.java
 *
 * Created on January 27, 2013, 10:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.custom;

import com.rameses.osiris3.data.DsServerResource;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.PluginContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.core.SharedContext;
import com.rameses.util.URLDirectory;
import com.rameses.util.URLDirectory.URLFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Elmo
 */
public class CustomOsirisServer extends OsirisServer {
    
    public CustomOsirisServer(String s, Map conf) {
        super(s,conf);
        super.addContextProvider( AppContext.class, new CustomAppContextProvider(this) );
        super.addContextProvider( SharedContext.class, new CustomSharedContextProvider(this) );
        super.addContextProvider( PluginContext.class, new CustomPluginContextProvider(this ) );
        super.addResource( DsServerResource.class, new DsServerResource(this) );
    }
    
    public void init() throws Exception {
        //load all apps
        final List<URL> appUrls = new ArrayList();
        final List<String> list = new ArrayList();
        URLDirectory dir = new URLDirectory(new URL(getRootUrl()+"/apps"));
        dir.list( new URLFilter(){
            public boolean accept(URL u, String filter) {
                if(filter.endsWith("/")) filter = filter.substring(0, filter.length()-1);
                list.add(filter.substring( filter.lastIndexOf("/")+1));
                appUrls.add( u ); 
                return false;
            }
        });
        
        boolean autoload = true;
        if( conf.containsKey("autoload") ) {
            try {
                autoload = Boolean.parseBoolean( conf.get("autoload")+"" );
            } catch(Exception ign){;}
        }
        
        if(autoload) {
            System.out.println("Loading apps");
            
            URL[] urls = appUrls.toArray(new URL[]{}); 
            System.getProperties().put( APP_URLS_PROPERTY, urls); 
            
            ExecutorService svc = Executors.newCachedThreadPool();
            for(final String s: list) {
                System.out.println(s);
                svc.submit( new Runnable(){
                    public void run() {
                        getContext(AppContext.class, s);
                    }
                });
            }
            svc.shutdown();
        }
    }
}
