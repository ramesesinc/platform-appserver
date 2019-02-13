/*
 * Channel.java
 *
 * Created on January 3, 2013, 7:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import com.rameses.util.MessageObject;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.websocket.WebSocket;

/**
 *
 * @author Elmo
 */
public abstract class Channel 
{    
    private String name;
    private String id;
    private Map conf;
    
    public Channel(String name) {
        this(name, null);
    }
    
    public Channel(String name, Map conf) {
        this.name = name;
        this.id = "WSCHANNEL" + new UID();        
        this.conf = (conf == null? new HashMap(): conf); 
    }    
    
    public abstract void removeSocket(WebSocket.Connection conn);
    public abstract void send(MessageObject msgobj);
    public abstract void close(int status, String msg );

    public String getName() { return name; }
    public String getId() { return id; }
    
    public Map getConf() { return conf; }
    
    public String getGroup() {
        return getProperty("group"); 
    }
    
    public String getProperty(String name) {
        Map conf = getConf();
        Object value = (conf == null? null: conf.get(name));
        return (value == null? null: value.toString()); 
    }
    
    
    public abstract ChannelGroup addGroup(String name); 
    public abstract ChannelGroup getGroup(String name); 
    public abstract String[] getGroupNames(); 
    
    public static class Connection 
    {
        private WebSocket.Connection conn;
        private String connid;
        
        public Connection(WebSocket.Connection conn, String connid) {
            this.conn = conn;
            this.connid = connid;
        }
        
        //public WebSocket.Connection getConnection() { return conn; } 
        public String getConnectionId() { return connid; } 
        public final boolean isClosed() { 
            return (conn == null); 
        } 
        
        public boolean accept(WebSocket.Connection source) {
            if (conn == null && source == null) {
                return true;
            } else if (conn != null) {
                return conn.equals(source); 
            } else if (source != null) {
                return source.equals(conn); 
            } else {
                return false; 
            }
        }
                
        public void send(MessageObject msgobj) throws IOException {
            if (conn == null) return; 
            
            if (!(connid+"").equals(msgobj.getConnectionId())) { 
                byte[] bytes = msgobj.encrypt(); 
                conn.sendMessage(bytes, 0, bytes.length); 
            }
        }        
        
        public void close(int status, String msg ) {
            if (conn == null) return;
            
            try { 
                conn.close(status, msg);
            } catch(Throwable t) {
                //do nothing 
            } finally {
                conn = null; 
            }
        }
    }
}
