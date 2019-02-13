/*
 * XConnectionContextResource.java
 *
 * Created on February 24, 2013, 7:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import com.rameses.osiris3.core.ContextResource;
import com.rameses.util.ConfigProperties;
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
public class XConnectionContextResource extends ContextResource {
    
    private Map<String, XConnectionProvider> providers = Collections.synchronizedMap( new HashMap());
    
    public void init() {
        Iterator<XConnectionProvider> iter = Service.providers( XConnectionProvider.class, getClass().getClassLoader() );
        while(iter.hasNext()) {
            XConnectionProvider xp = iter.next();
            xp.setContext( context );
            providers.put( xp.getProviderName(), xp );
        }
    }
    
    public Class getResourceClass() {
        return XConnection.class;
    }
    
    public XConnectionProvider getProvider(String name) {
        return providers.get(name); 
    }
    
    protected XConnection findResource(String key) {
        try {
            if(key.startsWith("default-")) {
                XConnection xc = providers.get(key).createConnection(key, null);
                if(xc==null)
                    throw new Exception("connection key "+key+ " not found!");
                return xc; 
            }
            
            String resourceName = key.split(":")[0];
            URL u = new URL(context.getRootUrl() +  "/connections/" + resourceName );
            if (u == null) throw new Exception("Connection " + resourceName + " not found");

            InputStream inp  = u.openStream();
            Map conf = ConfigProperties.newParser().parse(inp, context.getConf()); 
            
            //load the connection
            String providerType = (String) conf.get("provider");
            if( providerType == null || providerType.trim().length()==0)
                throw new Exception("Provider must be specified for connection " + key);
            
            XConnectionProvider cp = providers.get( providerType );
            if ( cp == null ) throw new IllegalStateException("'"+ providerType +"' connection provider not found"); 
            
            XConnection conn = cp.createConnection( key, conf );
            conn.start(); 
            return conn; 
            
        } catch(FileNotFoundException nfe) {
            //attempt to find the default. we do this by appending default to the key
            String newKey = "default-"+key;
            XConnectionProvider xcp = providers.get(newKey);
            XConnection xc = (xcp == null? null: xcp.createConnection(newKey, null));
            if (xc == null) throw new RuntimeException("connection key "+key+ " not found!");
            
            return xc;
            
        } catch(RuntimeException re) {
            throw re;
            
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    } 
    
    
} 
