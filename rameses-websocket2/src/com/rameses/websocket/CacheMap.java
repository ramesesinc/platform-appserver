/*
 * CacheMap.java
 *
 * Created on April 15, 2014, 11:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author wflores 
 */
class CacheMap implements Map 
{
    private final static Object CACHE_LOCK = new Object();
    private final static Timer CACHE_TIMER = new Timer();

    private ConcurrentHashMap<Object,TimeoutEntry> cache;
    private CleanupTask cleanupTask;            
    private int timeout; 
            
    public CacheMap() {
        timeout = 60000 * 5;         
        cache = new ConcurrentHashMap();
        cleanupTask = new CleanupTask();  
        CACHE_TIMER.schedule(cleanupTask, 60000, 60000); 
    }
    
    public int size() {
        return cache.size(); 
    }

    public boolean isEmpty() {
        return cache.isEmpty(); 
    }

    public boolean containsKey(Object key) {
        return cache.containsKey(key); 
    }

    public boolean containsValue(Object value) {
        return cache.containsValue(value); 
    }

    public Object get(Object key) {
        synchronized (CACHE_LOCK) { 
            if (key == null) return null;
            
            TimeoutEntry e = cache.get(key); 
            if (e == null) return null; 
            if (e.isExpired()) {
                cache.remove(key); 
                return null;
            } else { 
                return e.getValue(); 
            } 
        }
    }

    public Object put(Object key, Object value) {
        synchronized (CACHE_LOCK) { 
            return putImpl(key, value); 
        }
    }

    private Object putImpl(Object key, Object value) {
        long now = System.currentTimeMillis(); 
        TimeoutEntry e = cache.get(key);
        if (e == null) { 
            e = new TimeoutEntry(key, value, now+timeout); 
            cache.put(key, e); 
        } 
        if (e.isExpired()) { 
            e.setValue(value); 
        } 
        e.updateExpiry(now+timeout); 
        return e.getValue();     
    }        

    public Object remove(Object key) { 
        synchronized (CACHE_LOCK) { 
            TimeoutEntry e = cache.remove(key); 
            return (e == null? null: e.getValue()); 
        }
    }

    public void putAll(Map map) {
        synchronized (CACHE_LOCK) { 
            if (map == null) return; 

            Iterator keys = map.keySet().iterator();
            while (keys.hasNext()) {
                Object key = keys.next(); 
                Object val = map.get(key); 
                putImpl(key, val); 
            }
        }
    }

    public void clear() {
        cache.clear(); 
    }

    public Set keySet() {
        return cache.keySet(); 
    }

    public Collection values() {
        return cache.values();
    }

    public Set entrySet() {
        return cache.entrySet(); 
    } 
    
    public void close() {
        try { cache.clear(); }catch(Throwable t){;} 
        try { cleanupTask.cancel(); }catch(Throwable t){;} 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" TimeoutEntry "> 
    
    private class TimeoutEntry 
    {
        private Object key = null;
        private Object value = null; 
        private long expiry;
        
        TimeoutEntry(Object key, Object value, long expiry) {
            this.key = key;
            this.value = value; 
            this.expiry = expiry; 
        }

        public long getExpiry() { return expiry; }         
        public Object getKey() { return key; } 
        
        public Object getValue() { return value; } 
        public void setValue(Object value) {
            this.value = value;
        }
        
        public boolean isExpired() {
            return (expiry < System.currentTimeMillis()); 
        }
        
        public void updateExpiry(long expiry) {
            this.expiry = expiry; 
        }
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" CleanupTask "> 

    private class CleanupTask extends TimerTask 
    {
        public void run() {
            try {
                List<TimeoutEntry> removes = new ArrayList();
                Iterator<TimeoutEntry> values = cache.values().iterator();
                while (values.hasNext()) {
                    TimeoutEntry e = values.next(); 
                    if (e.isExpired()) removes.add(e); 
                }
                 
                while (!removes.isEmpty()) {
                    TimeoutEntry e = removes.remove(0); 
                    if (e.isExpired()) cache.remove(e.getKey()); 
                }
            } catch(Throwable t) {;} 
        }
    }

    // </editor-fold>    
}
