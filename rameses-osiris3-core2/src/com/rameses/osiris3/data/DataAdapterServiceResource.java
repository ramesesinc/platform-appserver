/*
 * SchemaSourceServiceResource.java
 *
 * Created on January 29, 2013, 3:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.data;

import com.rameses.osiris3.common.AppSettings;
import com.rameses.osiris3.common.ModuleFolder;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.ContextResource;
import com.rameses.osiris3.core.ResourceNotFoundException;
import com.rameses.osiris3.core.SharedContext;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Elmo
 */
public class DataAdapterServiceResource extends ContextResource {
    
   
    public void init() {
        //do nothing
    }
    
    public Class getResourceClass() {
        return DataAdapter.class;
    }

    
    private DataAdapter findDataAdapter(String name) {
        URL rootURL = null; 
        try {
            rootURL = new URL(context.getRootUrl());
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex); 
        }

        String rootURLPath = rootURL.toString();
        String appName = rootURLPath.substring( rootURLPath.lastIndexOf('/')+1); 
        AppSettings.AppConf appConf = AppSettings.getConf( appName ); 
        
        String[] fnames = new String[]{ "/adapters/", "/adapters/ext/" };  
        InputStream inp = null; 
        for (String fn : fnames ) {
            try {
                inp = new URL(context.getRootUrl() + fn + name ).openStream();
                if ( inp != null ) break; 
            } 
            catch(Throwable t){;} 
        }

        if ( inp == null ) {
            ModuleFolder mf = appConf.getModuleFolder();
            inp = mf.findResourceAsStream( "/adapters/"+ name ); 
        }
        
        if ( inp == null ) {
            throw new ResourceNotFoundException("'"+name+"' adapter not found");
        }
        
        try {
            Properties props = new Properties();
            props.load( inp );
            return new DataAdapter(props);
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try { inp.close(); } catch(Throwable e){;}
        }
    }
    
    protected DataAdapter findResource(String name) {
        if( context instanceof AppContext  ) {
            try {
                SharedContext sc = ((AppContext)context).getSharedContext();
                if(sc!=null)
                    return sc.getResource( DataAdapter.class, name );
                else
                    return findDataAdapter(name);
            } catch(ResourceNotFoundException rfe) {
                return findDataAdapter(name);
            }
        } else {
            return findDataAdapter(name);
        }
        
    } 

   
}
