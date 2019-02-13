/*
 * SchemaSourceServiceResource.java
 *
 * Created on January 29, 2013, 3:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.data;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.ContextResource;
import com.rameses.osiris3.core.ResourceNotFoundException;
import com.rameses.osiris3.core.SharedContext;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

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
        InputStream is = null;
        try {
            URL u = new URL(context.getRootUrl() + "/adapters/" + name);
            is = u.openStream();
            Properties props = new Properties();
            props.load( is );
            return new DataAdapter(props);
        } catch(FileNotFoundException fe) {
            throw new ResourceNotFoundException("'"+name+"' adapter not found");
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try { is.close(); } catch(Exception e){;}
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
