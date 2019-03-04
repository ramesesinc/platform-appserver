/*
 * DsServerResource.java
 *
 * Created on January 30, 2013, 11:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.data;

import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.core.ServerResource;
import com.rameses.util.ConfigProperties;

import com.rameses.util.Service;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class DsServerResource extends ServerResource {
    
    private Map<String, AbstractDataSource> ds = Collections.synchronizedMap(new HashMap());
    private DsProvider dsProvider;
    
    /** Creates a new instance of DsServerResource */
    public DsServerResource(OsirisServer s) {
        super(s);
    }
    
    public final AbstractDataSource getDataSource(String name) {
        if(!ds.containsKey(name)) {
            Map map = getDataInfo(name);
            ds.put( name, createDataSource(name, map) );
        }
        return ds.get(name);
    }
    
    public Map getDataInfo(String name) {
        
        try {
            String rootUrl = server.getRootUrl();
            URL url = new URL( rootUrl +  "/datasources/" + name );
            return ConfigProperties.newParser().parse(url, null); 
        } 
        catch(Throwable t) { 
            if ( t instanceof RuntimeException ) {
                throw (RuntimeException) t;
            }
            if ( t instanceof FileNotFoundException) {
                throw new RuntimeException("'"+name+"' datasource not found"); 
            }
            
            throw new RuntimeException(t); 
        } 
    }
    
    //This will attempt to locate plugins through META-INF/services if any is found.
    public AbstractDataSource createDataSource(String name, Map info) {
        if(dsProvider==null) {
            Iterator<DsProvider> iter = Service.providers( DsProvider.class, server.getClass().getClassLoader() );
            AbstractDataSource ds = null;
            if( iter.hasNext()) {
                dsProvider = iter.next();
            } else {
                dsProvider = new SimpleAbstractDsProvider();
            }
            System.out.println("*Datasource connection pool provider: " + dsProvider);
        }
        return dsProvider.createDataSource(name, info );
    }

    public void destroy() {
        for (AbstractDataSource ads : this.ds.values()) {
            try { 
                ads.destroy(); 
            } catch(Exception e) {
                System.out.println("failed to destory caused by " + e.getMessage());
            }
        }
    } 
}
