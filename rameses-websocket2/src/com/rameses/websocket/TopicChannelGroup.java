/*
 * TopicChannelGroup.java
 *
 * Created on July 24, 2014, 12:23 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import com.rameses.util.MessageObject;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.jetty.websocket.WebSocket;

/**
 *
 * @author wflores
 */
class TopicChannelGroup extends ChannelGroup 
{
    private Set<Channel.Connection> connections = new CopyOnWriteArraySet();
    
    public TopicChannelGroup(String name) {
        super(name); 
    }

    public void addSocket(WebSocket.Connection conn) { 
        String connid = new UID().toString(); 
        addSocket(conn, connid); 
    } 
    
    public void addSocket(WebSocket.Connection conn, String connid) { 
        connections.add(new Channel.Connection(conn, connid)); 
    } 

    public void removeSocket(WebSocket.Connection conn) { 
        if (conn == null) return; 
        
        List removelist = new ArrayList();
        for (Channel.Connection info: connections) {
            if (info.accept(conn)) {
                removelist.add(info);
            } 
        } 
        
        connections.removeAll(removelist); 
        removelist.clear(); 
    }

    public void send(MessageObject msgobj) {
        for (Channel.Connection info: connections) {
            try {
                info.send(msgobj); 
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }  
    }

    public void close(int status, String msg) {
        List removelist = new ArrayList();
        for (Channel.Connection info: connections) {
            info.close(status, msg); 
            removelist.add(info); 
        } 
        connections.removeAll( removelist ); 
        removelist.clear(); 
    } 
    
    public boolean isEmpty() { 
        return connections.isEmpty(); 
    } 
}
