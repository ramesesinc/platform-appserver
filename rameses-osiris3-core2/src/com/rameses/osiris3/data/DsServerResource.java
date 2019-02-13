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

import com.rameses.util.Service;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

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
        InputStream is = null;
        try {
            String rootUrl = server.getRootUrl();
            is = new URL( rootUrl +  "/datasources/" + name ).openStream();
            return new Config().read(is); 
        } catch(FileNotFoundException fnfe) {
            throw new RuntimeException("'"+name+"' datasource not found"); 
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {is.close();}catch(Exception ex){;}
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
    
    
    private class Config extends Properties 
    {
        private Map conf = new LinkedHashMap();
        
        public Map read(InputStream inp) throws IOException {
            super.load(inp);
            return conf; 
        }
        
        public Object put(Object key, Object value) {
            conf.put(key, value);             
            return super.put(key, value); 
        } 
    }     
}
