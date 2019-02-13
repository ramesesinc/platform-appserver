/*
 * AsyncQueue.java
 *
 * Created on May 27, 2014, 3:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author compaq
 */
public class AsyncQueue 
{
    private static Map<String,LinkedBlockingQueue> cache = new Hashtable();
    private static int timeout = 30000;
    
    public static void register(String name) {
        if (cache.containsKey(name)) return;
        
        cache.put(name, new LinkedBlockingQueue()); 
    }
    
    public static void unregister(String name) {
        cache.remove(name); 
    }    
    
    public static void put(String name, Object value) {
        LinkedBlockingQueue q = cache.get(name); 
        if (q != null) q.add(value); 
    } 
    
    public static Object poll(String name) throws Exception {
        try { 
            LinkedBlockingQueue q = cache.get(name); 
            Object result = q.poll(timeout, TimeUnit.MILLISECONDS);
            return result; 
        } catch(InterruptedException ie) {
            throw new TimeoutException(ie.getMessage());
        }
    }     
    
    public AsyncQueue() {
    }
    
}
