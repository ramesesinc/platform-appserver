/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.server.wss;

import com.rameses.http.HttpClient;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.xconnection.XConnection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores 
 */
public class SocketConnectionsImpl extends XConnection {

    private AbstractContext context; 
    private String name;
    private Map conf; 
    
    SocketConnectionsImpl( AbstractContext context, Map conf, String name ) {
        this.context = context; 
        this.conf = conf;
        this.name = name; 
        System.out.println(conf);
    }
    
    public void start() {
        //do nothing
    }

    public void stop() {
        //do nothing
    }

    public Map getConf() {
        return conf; 
    }
    
    public boolean exist( String channel, String channelgroup ) { 
        String host = getString(getConf(), "http.host"); 
        try {
            Map data = new HashMap();
            data.put("method", "exist"); 
            data.put("channel", channel); 
            data.put("channelgroup", channelgroup); 
            
            HttpClient httpc = createHttpClient( host ); 
            Object resp = httpc.post("manage", data); 
            if ( resp instanceof Boolean ) {
                return ((Boolean)resp).booleanValue(); 
            } else {
                return "true".equals( resp+"" ); 
            }
        } catch(Exception ex) { 
            System.out.println("HttpClientConnection.execute: error in posting data to " + host + " caused by " + ex.getMessage());
            return false; 
        } 
    }
    
    public Map getChannelGroups() {
        String host = getString(getConf(), "http.host"); 
        try {
            Map data = new HashMap();
            data.put("method", "getChannelGroups"); 
            
            HttpClient httpc = createHttpClient( host ); 
            Object result = httpc.post("manage", data); 
            if ( result instanceof Exception ) { 
                ((Exception) result).printStackTrace(); 
                return null; 
            } else {
                return (Map) result; 
            } 
        } catch(Exception ex) { 
            System.out.println("HttpClientConnection.execute: error in posting data to " + host + " caused by " + ex.getMessage());
            return null; 
        } 
    }
    
    private HttpClient createHttpClient( String host ) {
        Map conf = getConf(); 
        HttpClient httpc = new HttpClient(host, true);
        
        Number num = getNumber(conf, "http.connectionTimeout");
        if ( num != null ) { 
            httpc.setConnectionTimeout( num.intValue() ); 
        }
        num = getNumber(conf, "http.readTimeout");
        if ( num != null ) { 
            httpc.setReadTimeout( num.intValue() ); 
        }
        
        String str = getString(conf, "http.protocol");
        if ( str != null ) { 
            httpc.setProtocol( str ); 
        }
        
        try {
            boolean b = "true".equals(conf.get("http.encrypted")+"");  
            httpc.setEncrypted(true);
        } catch(Exception ign){
            httpc.setEncrypted(false); 
        }   
        return httpc;
    }    
    
    private String getString( Map conf, Object key ) {
        Object val = (conf == null ? null: conf.get(key)); 
        return (String) val; 
    }
    private Number getNumber( Map conf, Object key ) {
        Object val = (conf == null ? null: conf.get(key)); 
        if ( val == null ) {
            return null; 
        } else if (val instanceof Number) {
            return (Number)val;
        } else {
            return new Integer( val.toString() ); 
        } 
    } 
}
