/*
 * AppContext.java
 *
 * Created on January 26, 2013, 8:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

import com.rameses.io.LineReader;
import com.rameses.io.LineReader.Handler;
import com.rameses.util.ScanFileFilter;
import com.rameses.util.URLDirectory;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class AppContext extends MainContext {
    
    public AppContext(OsirisServer s, String name) {
        super(s);
        super.setName( name );
    }
    
    public SharedContext getSharedContext() {
        String sname =(String) super.getConf().get("shared");
        if(sname==null) return null;
        return server.getContext( SharedContext.class, sname );
    }
    
    public Map findProperties(String name) {
        InputStream is = null;
        try {
            is = super.getClassLoader().getResourceAsStream(name);
            if(is==null)
                is = getSharedContext().getClassLoader().getResourceAsStream(name);
            if(is==null)
                return null;
            final LinkedHashMap map = new LinkedHashMap();
            LineReader rdr = new LineReader();
            rdr.read(is, new Handler(){
                public void read(String text) {
                    int pos = text.indexOf("=");
                    String k = text.substring(0,pos).trim();
                    String v = text.substring(pos+1).trim();
                    map.put(k,v);
                }
            });
            return map;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {is.close();} catch(Exception ign){;}
        }
    }
    
    public void scanFiles(String path, ScanFileFilter scanFilter) throws Exception {
        scanFiles(path, scanFilter, null);
    }
    public void scanFiles(String path, ScanFileFilter scanFilter, String filePath) throws Exception {
        CustomURLScanFilter usf = new CustomURLScanFilter();
        usf.scanFilter = scanFilter;
        usf.filePathFilter = filePath;
        ClassLoader loader = getClassLoader();
        Enumeration<URL> e = loader.getResources( path );
        
        while(e.hasMoreElements()) {
            URLDirectory ud = new URLDirectory(e.nextElement());
            ud.list( usf, loader );
        }
        //scan also shared context
        if( getSharedContext() != null ) {
            loader = getSharedContext().getClassLoader();
            if( loader != null ) { 
                e = loader.getResources( path );
                while(e.hasMoreElements()) {
                    URLDirectory ud = new URLDirectory(e.nextElement());
                    ud.list( usf, loader );
                }
            }
        }
    }
    
    private class CustomURLScanFilter implements URLDirectory.URLFilter {
        private ScanFileFilter scanFilter;
        private String filePathFilter;
        public boolean accept(URL u, String filter) {
            if(filePathFilter!=null && !u.getFile().matches(filePathFilter)) return false;
            Map map = new HashMap();
            map.put("url", u);
            boolean directory = false;
            String filename = u.getFile();
            if(filename.endsWith("/")) {
                directory = true;
                filename = filename.substring(0, filename.length()-1);
                filename = filename.substring(filename.lastIndexOf("/")+1);
            } else {
                filename = filename.substring(filename.lastIndexOf("/")+1);
            }
            map.put("directory", directory);
            map.put("filename", filename);
            scanFilter.handle( map );
            return false;
        }
    }
}
