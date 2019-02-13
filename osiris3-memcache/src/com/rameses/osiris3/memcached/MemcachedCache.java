/*
 * MemcachedCache.java
 *
 * Created on February 9, 2013, 10:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.memcached;

import com.rameses.osiris3.cache.BlockingCache;
import com.rameses.osiris3.cache.CacheConnection;
import com.rameses.osiris3.cache.ChannelNotFoundException;
import com.rameses.util.Base64Cipher;

import java.net.InetSocketAddress;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

/**
 *
 * @author Elmo
 */
public class MemcachedCache extends BlockingCache implements CacheConnection  {
    
    private final static String DEFAULT_PORT = "11211";
    
    private Map conf;
    private String name;
    private MemcachedClient client;
    
    private int defaultTimeout;
    
    /**
     * Creates a new instance of MemcachedCache
     */
    public MemcachedCache(String name, Map props) {
        this.name = name;
        this.conf = props;
        this.defaultTimeout = 60;
    }
    
    public Map getConf() {
        return conf;
    }

    public void start() {
        try {
            super.start(); 

            String host = getProperty("host"); 
            if ( host == null || host.trim().length()==0 ) 
                throw new IllegalStateException("host is required in your memcached connection setting"); 

            Number num = convertInt( getProperty("timeout")); 
            this.defaultTimeout = (num == null ? 60 : num.intValue()); 
            
            String[] arr = host.trim().split(":");
            StringBuilder sb = new StringBuilder(); 
            sb.append( arr[0]).append(":");
            if ( arr.length <= 1 ) { 
                sb.append( DEFAULT_PORT ); 
            } else {
                num = convertInt( arr[1]); 
                sb.append( num==null ? DEFAULT_PORT : num.toString() );
            }
            
            client = new MemcachedClient(AddrUtil.getAddresses( sb.toString())); 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public void stop() { 
        try { 
            super.stop(); 
        } catch(Throwable t){;}  
        
        try { 
            client.shutdown(); 
        } catch(Throwable t) {
            //do nothing 
        } finally {
            client = null; 
        }
    }
    
    public Object get(String name) {  
        if ( name == null ) { 
            return null;
        } 
        
        Object data = client.get( name ); 
        if ( data instanceof String ) {
            Base64Cipher cipher = new Base64Cipher(); 
            if ( cipher.isEncoded( data.toString())) {
                return cipher.decode( data.toString()); 
            } 
        }
        return data; 
    }
    
    public Object put(String name, Object data) {
        return put( name, data, this.defaultTimeout ); 
    }

    public Object put(String name, Object data, int timeout) { 
        if ( name == null ) { 
            return null;
        } else if ( data == null ) { 
            return client.delete( name ); 
        } 
        
        String encstr = null; 
        Base64Cipher cipher = new Base64Cipher(); 
        if ( data instanceof String ) { 
            if ( cipher.isEncoded( data.toString() )) {
                encstr = data.toString(); 
            } else {
                encstr = cipher.encode( data );  
            } 
        } else {
            encstr = cipher.encode( data ); 
        } 
        return client.set( name, timeout, encstr );
    }    
    
    public void remove(String name) { 
        if ( name != null ) {
            client.delete( name );
        }
    }
    
    public void createBulk(String id, int timeout, int options) {
    }
    public void appendToBulk(String bulkid, String newKeyId, Object data) {
    }

    public Map<String, Object> getBulk(String bulkid, int timeout) { 
        return null; 
    }

    private String getProperty( String name ) {
        Object value = (conf == null? null: conf.get(name)); 
        return (value == null ? null: value.toString()); 
    }
    private Number convertInt( String value ) {
        try {
            return new Integer( value ); 
        } catch(Throwable t) {
            return null; 
        }
    }
}
