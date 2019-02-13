/*
 * ScriptDocHelper.java
 *
 * Created on February 25, 2013, 4:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.SharedContext;
import com.rameses.util.URLDirectory;
import com.rameses.util.URLDirectory.URLFilter;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Elmo
 */
public class ScriptDocHelper {
    
    public static class ScriptName {
        private String name;
        private URL url;
        private String context;
        public ScriptName(String name, String context,URL u) {
            this.name = name;
            this.context = context;
            this.url = u;
        }
        public String getName() {
            return name;
        }
        public URL getUrl() {
            return url;
        }
        
        public int hashCode() {
            return name.hashCode();
        }
        
        public boolean equals(Object obj) {
            return hashCode() == obj.hashCode();
        }
        
        public String toString() {
            return this.name;
        }

        public String getContext() {
            return context;
        }
    }
    
    private static void fetchInfo(AbstractContext ctx, final String c, final Set set,  URL u ) throws Exception {
        URLDirectory ud = new URLDirectory(u);
        ud.list( new URLFilter(){
            public boolean accept(URL url, String filter) {
                if(filter.trim().length()==0) return false;
                if(filter.endsWith("/")) return false;
                String name = filter.substring(filter.lastIndexOf("/")+1);
                set.add( new ScriptName(name, c, url) );
                return false;
            }
        }, ctx.getClassLoader() );
    }
    
    public static Set<ScriptName> getAllScripts( AppContext ctx ) {
        LinkedHashSet<ScriptName> hset = new LinkedHashSet();
        SharedContext sc = ctx.getSharedContext();
        if( sc!=null ) {
            try {
                Enumeration<URL> urls = sc.getClassLoader().getResources( "scripts" );
                while(urls.hasMoreElements()) {
                    fetchInfo(sc, "shared", hset, urls.nextElement());
                }
            } catch(Exception ign){;}
        }
        
        try {
            Enumeration<URL> urls = ctx.getClassLoader().getResources( "scripts" );
            while(urls.hasMoreElements()) {
                fetchInfo(sc,"app", hset, urls.nextElement());
            }
        } catch(Exception ign){;}
        
        return  hset;
    }
    
}
