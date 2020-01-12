/*
 * DsServerResource.java
 *
 * Created on January 30, 2013, 11:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.data;

import com.rameses.osiris3.common.ModuleFolder;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.core.ServerResource;
import com.rameses.util.ConfigProperties;

import com.rameses.util.Service;
import java.io.InputStream;
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
        String[] fnames = new String[]{ "/datasources/", "/datasources/ext/" };  
        String rootUrl = server.getRootUrl();
        InputStream inp = null;         
        for (String fn : fnames ) {
            try {
                inp = new URL(rootUrl + fn + name ).openStream();
                if ( inp != null ) break; 
            } 
            catch(Throwable t){;} 
        }

        if ( inp == null ) {
            Object oo = System.getProperties().get( OsirisServer.APP_URLS_PROPERTY ); 
            if ( oo instanceof URL[] ) {
                URL[] urls = (URL[]) oo; 
                for (URL uu : urls) {
                    if ( inp != null ) { break; }
                    
                    ModuleFolder mf = new ModuleFolder( uu.toString() + "modules" );
                    if ( mf.exist()) {
                        inp = mf.findResourceAsStream("datasources/" + name); 
                    }
                }
            }
        }
        
        if ( inp == null ) { 
            throw new RuntimeException("'"+name+"' datasource not found");
        } 
        
        try {
            return ConfigProperties.newParser().parse(inp, null); 
        } finally {
            try { inp.close(); } catch(Throwable t){;} 
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
