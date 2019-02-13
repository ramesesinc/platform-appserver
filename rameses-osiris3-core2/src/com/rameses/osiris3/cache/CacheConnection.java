/*
 * CacheConnection.java
 *
 * Created on February 9, 2013, 7:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.cache;

import java.util.Map;

/**
 *
 * @author Elmo
 */
public interface CacheConnection {
    
    public static String CACHE_KEY = "cache";
    
    Object get(String name);
    
    //this is for a blocking get. timeout in seconds
    Object get(String name, int timeout) throws Exception;
    
    Object put(String name, Object data, int timeout);
    Object put(String name, Object data);
    void remove(String name);
    
    //this is intended for multiple data caches
    void createBulk(String id, int timeout, int options);
    void appendToBulk( String bulkid, String newKeyId, Object data );
    Map<String, Object> getBulk(String bulkid, int timeout );
    
   
    
}
