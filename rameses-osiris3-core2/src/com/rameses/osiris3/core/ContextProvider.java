/*
 * ContextProvider.java
 *
 * Created on January 28, 2013, 7:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

import com.rameses.util.ConfigProperties;
import com.rameses.util.URLDirectory;
import com.rameses.util.URLDirectory.URLFilter;
import groovy.lang.GroovyClassLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class ContextProvider {
    
    protected abstract AbstractContext findContext(String name);
    protected abstract String getClassLoaderPath(String name);
    protected abstract String getConfUrl(String name);
    protected abstract void initContext(AbstractContext ac);
    public abstract String getRootUrl();
    
    protected OsirisServer server;
    
    private Map<String, AbstractContext> contexts = Collections.synchronizedMap( new HashMap() );
    
    public ContextProvider(OsirisServer server) {
        this.server = server;
    }
    
    public final AbstractContext getContext(String name) {
        if(!contexts.containsKey(name)) {
            Map conf = getConf(name);
            if ("false".equals( conf.get("enabled") )) {
                //this app has been disabled, exit right away 
                System.out.println("apps: "+ name +" has been disabled. please check the conf setting.");
                return null; 
            } 
            
            AbstractContext ac = findContext(name);
            ac.setName( name );
            ac.setConf( conf );
            ac.setClassLoader(getClassLoader(name) );
            ac.setRootUrl(getRootUrl()+"/"+name);
            initContext(ac);
            contexts.put(name, ac);
            ac.start();
        }
        return contexts.get(name);
    }
    
    protected OsirisServer getServer() {
        return server;
    }
    
    //INFORMATION OF THE CONTEXT
    protected Map getConf(String name) {
        try {
            String path = getConfUrl(name);
            if(path == null) return new HashMap(); 
            
            URL url = new URL( path );
            return ConfigProperties.newParser().parse(url, null); 
            
        } catch(Exception e) {
            if (e instanceof FileNotFoundException) {
                throw new RuntimeException("'"+name+"' conf not found"); 
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException)e; 
            } else {
                throw new RuntimeException(e.getMessage(), e);
            }
        } 
    }
    
    //CLASSLOADER OF THE CONTEXT
    protected  ClassLoader getClassLoader(String name) {
        URLClassLoader urc = null;
        try {
            
            final List<URL> urlList = new ArrayList();
            //we'll also add the modules.conf file.
            URL uf = new URL(getRootUrl() +"/"+ name + "/modules.conf");
            File f = new File(uf.getFile());
            if ( f.exists() ) {
                InputStream is = null;
                InputStreamReader isr = null;
                BufferedReader br = null;
                try {
                    is = new FileInputStream(f);
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    String s = null;
                    while( (s=br.readLine())!=null) { 
                        try {
                            if ( s.trim().length()== 0 ) continue;
                            else if ( s.startsWith("#")) continue; 
                            else if ( !s.endsWith("/")) s += "/";
                            
                            s = resolveValue(s, null); 
                            urlList.add(new URL( s ));
                        } catch(Throwable ign){ 
                            System.out.println("Error loading module "+ s +" caused by "+ ign.getMessage());
                        }
                    }
                } catch(Throwable t) {
                    System.out.println("Error loading module "+ uf +" caused by "+ t.getMessage());
                } finally { 
                    try {br.close();} catch(Exception ex){;} 
                    try {isr.close();} catch(Exception ex){;} 
                    try {is.close();} catch(Exception ex){;} 
                } 
            }
            
            //load modules directory
            String path = getClassLoaderPath(name);
            URLDirectory ud = new URLDirectory(new URL(path));
            ud.list(new URLFilter(){
                public boolean accept(URL u, String filter) {
                    if( (filter.endsWith(".jar") || filter.endsWith(".jar/"))) {
                        urlList.add(u);
                    }
                    return false;
                }
            });
            
            URL[] urls = urlList.toArray(new URL[]{});
            urc = new URLClassLoader(urls);           
            GroovyClassLoader gc = new GroovyClassLoader(urc);
            //add url so it can scan classes
            for( URL u : urls) { 
                gc.addURL( u ); 
            } 
            return gc;
        } catch(Exception ign){
            throw new RuntimeException("ERROR init classloader for "+ name +" "+ ign.getMessage());
        }
    }

    public void stop() {
        for(AbstractContext ac: this.contexts.values()) {
            try {
                ac.stop(); 
            } catch( Throwable e ) {
                System.out.println("failed to stop context provider " + ac.getName() + ", caused by " + e.getMessage());
            } 
        } 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" Helper methods ">     
    
    private String resolveValue(String value, Map[] refs) { 
        if ( value == null || value.trim().length()==0 ) { 
            return value;  
        } 
        
        if ( refs == null ) { 
            refs = new HashMap[]{}; 
        } 
        
        int startidx = 0; 
        boolean has_expression = false; 
        StringBuilder builder = new StringBuilder(); 
        String str = value;
        while (true) {
            int idx0 = str.indexOf("${", startidx);
            if (idx0 < 0) break;
            
            int idx1 = str.indexOf("}", idx0); 
            if (idx1 < 0) break;
            
            has_expression = true; 
            String skey = str.substring(idx0+2, idx1); 
            builder.append(str.substring(startidx, idx0)); 
            
            Object objval = null; 
            for (Map mref : refs) {
                if (mref == null) { continue; }
                
                objval = mref.get(skey); 
                if (objval != null) { break; }
            }
            
            if (objval == null) {
                objval = System.getProperty(skey);
            } 
            
            if (objval == null) { 
                builder.append(str.substring(idx0, idx1+1)); 
            } else { 
                builder.append(objval); 
            } 
            startidx = idx1+1; 
        } 
        
        if (has_expression) {
            builder.append(str.substring(startidx));  
            return builder.toString(); 
        } else {
            return value; 
        }
    }  
    
    // </editor-fold>
} 
