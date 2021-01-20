/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.common;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public final class AppSettings {
    
    private final static Object CONF_LOCKED = new Object();
    
    private final static Map confs = new HashMap();
    
    public static AppConf addConf( String name, Map conf, URL url ) {
        synchronized(CONF_LOCKED) { 
            AppConf ac = new AppConf();
            ac.name = name; 
            ac.conf = conf; 
            ac.url = url;
            confs.put( name, ac ); 
            return ac;
        }
    }
    
    public static AppConf getConf( String name ) {
        synchronized(CONF_LOCKED) { 
            Object o = confs.get( name ); 
            if ( o instanceof AppConf ) {
                return (AppConf) o;
            }
            else if ( o != null ) {
                confs.remove( name ); 
            }
            return null; 
        }
    }
    
    public static List<AppConf> getConfs() {
        synchronized(CONF_LOCKED) { 
            List list = new ArrayList();
            for (Object o : confs.values()) {
                if ( o instanceof AppConf) {
                    list.add( o ); 
                }
            }
            return list;
        }
    }
    
    
    public static class AppConf {
        
        private String name; 
        private Map conf;
        private URL url;
        
        public String getName() { return name; }
        public Map getConf() { return conf; } 
        public URL getURL() { return url; } 
        
        public String getProperty( String name ) {
            Object o = (conf == null ? null : conf.get(name)); 
            return ( o == null ? null : o.toString()); 
        }
        
        public URL getURL( String resourceName ) {
            if ( url == null ) return null; 
            if ( resourceName == null ) return null; 
            
            StringBuilder sb = new StringBuilder(); 
            sb.append( url.toString());
            if ( !sb.toString().endsWith("/")) {
                sb.append("/"); 
            }
            
            if ( resourceName.startsWith("/")) {
                sb.append( resourceName.substring(1)); 
            }
            else {
                sb.append( resourceName); 
            }
            
            try {  
                return new URL( sb.toString());
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);  
            }
        }
        
        public File getFile( String resourceName ) {
            URL url = getURL(resourceName);
            return toFile( url ); 
        }
        
        private File[] extdirs = null;
        
        public File[] getModuleExtDir() {
            if ( extdirs == null ) {
                List<File> dirs = new ArrayList();
                File fdir = getFile("modules/ext"); 
                if ( fdir != null ) dirs.add( fdir ); 
                
                String[] arr = new String[]{ getProperty("modules.ext.dir") }; 
                if ( arr[0] != null && arr[0].length() > 0 ) {
                    fdir = new File( arr[0]); 
                    if ( fdir.exists() && fdir.isDirectory()) {
                        dirs.add( fdir ); 
                    }
                } 
                extdirs = (File[]) dirs.toArray(new File[]{}); 
            } 
            return extdirs; 
        } 
        
        public URL toURL( File file ) {
            try {
                if ( file == null ) return null; 
                return file.toURI().toURL();
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        public File toFile( URL url ) {
            try {
                if ( url == null ) return null; 
                
                if ( "file".equals(url.getProtocol())) {
                    File ff = new File( url.toURI() ); 
                    if ( ff.exists() ) return ff; 
                }
                return null; 
            } 
            catch (URISyntaxException ex) {
                throw new RuntimeException( ex ); 
            }
        }
        
        public ModuleFolder getModuleFolder() {
            ModuleFolder mf = new ModuleFolder( getURL("modules"), getURL("plugins")); 
            mf.setModuleExtDir( getModuleExtDir()); 
            return mf; 
        }
    }
}
