/*
 * WebSocketMessageHandler.java
 *
 * Created on January 21, 2013, 3:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import com.rameses.http.HttpClient;
import com.rameses.util.AccessDeniedException;
import com.rameses.util.Base64Cipher;
import com.rameses.util.ExceptionManager;
import com.rameses.util.MessageObject;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.eclipse.jetty.websocket.WebSocket;

/**
 *
 * @author Elmo
 */
public class WebSocketMessageHandler implements WebSocket, WebSocket.OnTextMessage, WebSocket.OnBinaryMessage 
{
    private SocketConnections sockets;    
    private Map conf;
    private Properties headers;
    private Map cache;

    private String channelName;
    private String connectionid;
    private String ownerid;
    
    private WebSocket.Connection connection;
    private Channel channel;
    
    public WebSocketMessageHandler(SocketConnections conn, Map conf, Properties headers, Map cache) {
        this.sockets = conn; 
        this.conf = conf; 
        this.cache = cache;         
        this.headers = headers;         
        
        ownerid = "WSMH" + new UID();
        channelName = headers.getProperty("channel"); 
        connectionid = headers.getProperty("connectionid");
        if (connectionid == null) connectionid = "X"+ownerid; 
    }
    
    public void onOpen(WebSocket.Connection connection) {
        //on open add the channel if not yet exis
        //System.out.println("onOpen: connectionid="+connectionid + ", channel="+channelName);
        boolean authenticated = (cache.get(connectionid) != null); 
        boolean authEnabled = "true".equals(conf.get("auth.enabled")+"");        
        if (authEnabled && !authenticated) {
            try { 
                authenticate(); 
                cache.put(connectionid, connectionid); 
            } catch(Exception e) { 
                Exception x = ExceptionManager.getOriginal(e);
                if (x instanceof AccessDeniedException) {
                    System.out.println("[WebSocketMessageHandler, "+channelName+"] " + x.getMessage());
                    connection.close(1002, "AUTH_FAILED");
                } else {
                    connection.close(1000, "Failed caused by " + x.getMessage());
                } 
            } 
        } 
        
        try {
            this.connection = connection; 
            this.channel = sockets.getChannel( channelName );
            
            //System.out.println("connection_protocol-> " + this.connection.getProtocol());
            Properties props = decodeProtocol(this.connection.getProtocol()); 
            String group = props.getProperty("group", ""); 
            if (group.length() == 0) group = channelName;
            
            String[] gnames = group.split(","); 
            for (String gname: gnames) {
                if (gname.trim().length() == 0) continue;
                
                this.channel.addGroup(gname).addSocket(this.connection, this.connectionid); 
            }
        } catch(ChannelNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * if closing was due to a timeout, do not remove the channel yet as
     * it will still attempt to reconnect otherwise some messages might be missed.
     *
     */
    public void onClose(int i, String msg) {
        //System.out.println("onClose: connectionid="+connectionid + ", channel="+channelName);
        if(this.connection!=null) {
            channel.removeSocket( this.connection );
            this.connection = null;
        }
    }
    
    public void onMessage(String data) {
        //no implementation, we will only use array of bytes
    }
    
    public void onMessage(byte[] bytes, int offset, int length) {
        try {
            MessageObject mo = new MessageObject().decrypt(bytes, offset, length); 
            //if the sender and receiver uses the same connection, do not process
            if (ownerid.equals(mo.getConnectionId())) return; 

            String msggroup = mo.getGroupId();
            if (msggroup == null) msggroup = channelName;
            
            ChannelGroup cg = channel.getGroup(msggroup); 
            if (cg == null) {
                System.out.println("ChannelGroup '"+msggroup+"' not found in "+channelName+" channel");
            } else {
                cg.send( mo ); 
            }
        } catch(Exception e) {
            System.out.println("[WebSocketMessageHandler, "+ channelName +"] " + "onMessage failed caused by " + e.getMessage());
            e.printStackTrace();
        }         
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    private void authenticate() throws Exception {
        String authCluster = (String)conf.get("auth.cluster");        
        String authContext = (String)conf.get("auth.context");
        String authPort = (String)conf.get("auth.port");
        if (authCluster == null) authCluster = "osiris3";
        if (authContext == null) authContext = "default";
        if (authPort == null) authPort = "8070"; 
        
        String authPath = authCluster +"/services/"+ authContext  +"/WebsocketService.authenticate";
        HttpClient httpc = new HttpClient("localhost:"+authPort, true);
        httpc.post(authPath, new Object[]{ new Object[]{headers}, new HashMap()}); 
    }    
    
    private Properties decodeProtocol(String text) {
        Properties props = new Properties();
        String channel = null; 
        String extended = null; 
        try { 
            int idx = text.indexOf(';'); 
            if (idx > 0) {
                channel = text.substring(0, idx); 
                extended = text.substring(idx+1);
            } 

            Object obj = new Base64Cipher().decode(extended); 
            if (obj instanceof Map) {
                Map map = (Map)obj;
                Iterator keys = map.keySet().iterator();
                while (keys.hasNext()) {
                    Object key = keys.next();
                    Object val = map.get(key); 
                    if (val != null) props.put(key, val); 
                }
            }
        } catch(Throwable t) {;} 
        
        props.put("channel", channel); 
        return props; 
    } 
    
    // </editor-fold>
    
}
