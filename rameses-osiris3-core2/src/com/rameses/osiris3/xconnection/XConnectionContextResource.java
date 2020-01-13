/*
 * XConnectionContextResource.java
 *
 * Created on February 24, 2013, 7:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import com.rameses.osiris3.common.AppSettings;
import com.rameses.osiris3.common.AppSettings.AppConf;
import com.rameses.osiris3.common.ModuleFolder;
import com.rameses.osiris3.core.ContextResource;
import com.rameses.server.ServerConf;
import com.rameses.util.ConfigProperties;
import com.rameses.util.Service;
import java.io.FileNotFoundException;
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
            
            String rootURLPath = new URL(context.getRootUrl()).toString();
            String appName = rootURLPath.substring( rootURLPath.lastIndexOf('/')+1); 
            AppConf appConf = AppSettings.getConf( appName ); 
            
            String resourceName = key.split(":")[0];
            String[] folderNames = new String[]{ "/connections/", "/connections/ext/" };  
            InputStream inp = null; 
            for (String fn : folderNames ) {
                try {
                    inp = new URL(context.getRootUrl() + fn + resourceName ).openStream();
                    if ( inp != null ) break; 
                } 
                catch(Throwable t){;} 
            }

            if ( inp == null ) {
                ModuleFolder mf = appConf.getModuleFolder(); 
                inp = mf.findResourceAsStream( "/connections/"+ resourceName ); 
            }
            
            if (inp == null) {
                throw new Exception("Connection " + resourceName + " not found");
            }

            Map conf = null; 
            try {
                conf = ConfigProperties.newParser().resolver(new ResolverImpl()).parse(inp, context.getConf()); 
            } finally {
                try { inp.close(); } catch(Throwable t){;} 
            }
            
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
    
    private class ResolverImpl extends ConfigProperties.Resolver {
        public Object resolve(String name) {
            if ( name != null && name.startsWith("@@")) {
                String[] arr = name.split(":");
                String skey = (arr.length == 2 ? arr[1] : ""); 
                Map group = ServerConf.getGroup( name );
                return group.get( skey ); 
            }
            
            return super.resolve( name ); 
        }
    }
} 
