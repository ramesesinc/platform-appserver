/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.common;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
public class ModuleFolder {
    
    private URL url;
    private URI uri;
    
    private File moduleExtDir;
    
    public ModuleFolder( String urlPath ) {
        try { 
            url = new URL( urlPath );
        } catch (RuntimeException re) {
            throw re; 
        } catch(Throwable t) {
            throw new RuntimeException(t); 
        }
        
        this.uri = (url == null ? null : toURI(url)); 
    }
    
    public ModuleFolder( URL url ) {
        this.url = url;
        this.uri = (url == null ? null : toURI(url)); 
    }
    
    public boolean exist() {
        InputStream inp = null; 
        try {
            inp = url.openStream();
            return true; 
        } catch(Throwable t) {
            return false; 
        } finally {
            try { inp.close(); }catch(Throwable t){;} 
        }
    }
    
    public List<URL> getServices() {
        return list( new DefaultServiceFilter());
    }
    
    public File getModuleExtDir() { 
        return moduleExtDir; 
    }
    public void setModuleExtDir( File moduleExtDir ) {
        this.moduleExtDir = moduleExtDir;
    }
    
    public List<URL> getPluginServices() {
        URL baseURL = null; 
        File dir = getModuleExtDir();
        if ( dir != null && dir.isDirectory()) {
            baseURL = toURL( dir ); 
        }
        
        if ( baseURL == null ) {
            return new ArrayList();
        }
        
        PluginServiceFilter f = new PluginServiceFilter();
        listImpl( f, baseURL );
        return f.list; 
    }
    
    public URL findResource( String name ) {
        URL baseURL = null; 
        File dir = getModuleExtDir();
        if ( dir != null && dir.isDirectory()) {
            baseURL = toURL( dir ); 
        }
        
        if ( baseURL == null ) {
            return null; 
        }

        PluginResourceFilter filter = new PluginResourceFilter( name );
        listImpl( filter, baseURL );
        return filter.result; 
    }
        
    public InputStream findResourceAsStream( String name ) {
        URL uu = findResource(name); 
        if ( uu == null ) return null; 
        
        try {
            return uu.openStream(); 
        } catch (IOException ex) {
            throw new RuntimeException(ex); 
        }
    }
    
    public List<URL> list( ModuleFolder.Filter filter ) {
        return listImpl( filter, this.url ); 
    }
    
    private List<URL> listImpl( ModuleFolder.Filter filter, URL baseURL ) {
        if ( baseURL == null ) { 
            return new ArrayList();
        }
        String protocol = baseURL.getProtocol();
        if ( "file".equals( protocol)) {
            File dir = new File(toURI(baseURL)); 
            if ( !dir.exists() || !dir.isDirectory()) {
                return new ArrayList();
            }
            
            FileFilterImpl ff = new FileFilterImpl( filter );
            dir.listFiles( ff );
            return ff.getResults(); 
        }
        return new ArrayList(); 
    }
    
    public URL getURL( String name ) {
        if ( url == null ) { 
            return null; 
        }
        
        String protocol = url.getProtocol();
        if ( "file".equals( protocol)) {
            File dir = new File( uri );
            File file = new File( dir, name ); 
            if ( file.exists() ) {
                return toURL( file ); 
            }
        }
        return null; 
    }
        
    private URI toURI( URL url ) {
        try {
            return url.toURI(); 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e); 
        }        
    }
    
    private URL toURL( File file ) {
        try {
            return file.toURI().toURL(); 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e); 
        }        
    }
    
    private class FileFilterImpl implements FileFilter {

        private ModuleFolder.Filter handler;
        private List<URL> list; 
        
        FileFilterImpl( ModuleFolder.Filter handler ) {
            this.handler = handler; 
            this.list = new ArrayList();
        }
        
        void clear() {
            list.clear(); 
        }
        
        public List<URL> getResults() {
            return list; 
        }
        
        public boolean accept(File file) {
            URL url = null; 
            try {
                url = file.toURI().toURL(); 
            } catch(Throwable t) {
                t.printStackTrace(); 
                return false; 
            }
            
            if ( handler.accept(url, file.getName()) ) {
                list.add( url ); 
            }
            return false; 
        }
    }
    
    public static interface Filter {
        boolean accept( URL url, String name );
    }
    
    private class DefaultServiceFilter implements Filter {
        public boolean accept(URL url, String name) {
            return ( name.endsWith(".jar") || name.endsWith(".jar/"));
        }
    }

    private class PluginServiceFilter implements Filter {
        
        private List list = new ArrayList();
        
        public boolean accept(URL url, String name) {
            if ( name.endsWith(".jar") || name.endsWith(".jar/")) {
                return false; 
            }

            if ( "file".equals(url.getProtocol())) {
                File f = new File( toURI(url)); 
                if ( !f.isDirectory()) return false; 
                
                File ff = new File( f, "module.conf");
                if ( !ff.exists()) return false; 
                if ( ff.isDirectory() ) return false; 
                
                ff = new File( f, "services");
                if ( ff.exists() && ff.isDirectory()) {
                    list.add( toURL( ff ) ); 
                }
            }
            
            return false; 
        }
    }

    private class PluginResourceFilter implements Filter {
        
        private String resourceName;
        private URL result;
        
        PluginResourceFilter( String resourceName ) {
            this.resourceName = resourceName; 
        }
        
        public boolean accept(URL url, String name) {
            if ( result != null ) {
                return false; 
            }

            if ( name.endsWith(".jar") || name.endsWith(".jar/")) {
                return false; 
            }

            if ( "file".equals(url.getProtocol())) {
                File f = new File( toURI(url)); 
                if ( !f.isDirectory()) return false; 
                
                File ff = new File( f, "module.conf");
                if ( !ff.exists()) return false; 
                if ( ff.isDirectory() ) return false; 
                
                ff = new File( f, resourceName );
                if ( ff.exists() && !ff.isDirectory()) {
                    result = toURL( ff ); 
                }
            }
            
            return false; 
        }
    }
}
