/*
 * ContextResource.java
 *
 * Created on January 27, 2013, 8:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class ContextResource {
    
    static class CacheTreeLocked {}
    
    private final CacheTreeLocked CACHE_LOCKED = new CacheTreeLocked(); 
            
    protected OsirisServer server;
    protected AbstractContext context;
    protected Map<String, Object> resources = Collections.synchronizedMap(new HashMap());
    
    protected abstract <T> T findResource(String key);
    public abstract void init();
    public abstract Class getResourceClass();
    
    public boolean isCached() {
        boolean _cached = true;
        try {
            String t = System.getProperty("cached_resource");
            if(t!=null)_cached = Boolean.parseBoolean(""+t);
        }
        catch(Exception ign){;}
        return _cached;
    } 
    
    public final <T> T getResource(String key) {
        synchronized( CACHE_LOCKED ) {
            Object res = null;
            if( !resources.containsKey(key) ) {
                res = findResource(key);
                if(isCached()) {
                    resources.put(key,res);
                }
            } else {
                res = (T)resources.get(key);
            }
            return (T) res;
        }
    }
    
    public void remove(String key) {
        synchronized( CACHE_LOCKED ) {
            resources.remove( key );
        }
    }
    
    public void removeAll() {
        synchronized( CACHE_LOCKED ) {
            resources.clear();
        }
    }

    public void setServer(OsirisServer server) {
        this.server = server;
    }

    public void setContext(AbstractContext context) {
        this.context = context;
    }
    
    public void copyValues( List infos ) {
        synchronized( CACHE_LOCKED ) {
            infos.addAll( resources.values());
        }
    }
}
