/*
 * BasicWebsocketHandler.java
 *
 * Created on January 21, 2013, 3:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import com.rameses.util.Base64Cipher;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

/**
 *
 * @author Elmo
 */
public class BasicWebsocketHandler extends WebSocketHandler 
{
    private SocketConnections sockets;
    private Map conf; 
    
    private CacheMap cacheMap;    
    private Timer timer;
    
    public BasicWebsocketHandler(SocketConnections conn, Map conf) {
        this.sockets = conn;
        this.conf = conf;
        
        cacheMap = new CacheMap(); 
    }
    
    /***
     * check the id here
     */
    public WebSocket doWebSocketConnect(HttpServletRequest hreq, String protocol) {
        //protocol format: <channel>;<encrypted_additional_headers>       
        String channel = null;
        String extended = null;
        int idx = protocol.indexOf(';'); 
        if (idx > 0) {
            channel = protocol.substring(0, idx); 
            extended = protocol.substring(idx+1);
        } else {
            channel = protocol; 
        } 
        
        Properties headers = toProperties(extended); 
        headers.setProperty("channel", channel); 
        
        //test first if channel exists before creating the websocket
        if(sockets.isChannelExist( channel )) {
            return new WebSocketMessageHandler(sockets, conf, headers, cacheMap);
        }
        else {
            throw new RuntimeException("Channel " + channel + " does not exist!");
        }
    }
    
    public void close() {
        cacheMap.close();
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    private Properties toProperties(String text) {
        Properties props = new Properties();
        
        try { 
            Object obj = new Base64Cipher().decode(text); 
            if (obj instanceof Map) {
                Map map = (Map)obj;
                Iterator keys = map.keySet().iterator();
                while (keys.hasNext()) {
                    Object key = keys.next();
                    Object val = map.get(key); 
                    if (val != null) props.put(key, val); 
                }
            }
        } catch(Throwable t) {
            //do nothing 
        } finally {
            return props; 
        }
    } 
    
    // </editor-fold>
        
}
