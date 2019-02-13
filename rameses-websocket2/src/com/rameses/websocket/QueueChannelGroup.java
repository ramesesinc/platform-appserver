/*
 * QueueChannelGroup.java
 *
 * Created on July 24, 2014, 12:53 AM
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
import java.util.concurrent.LinkedBlockingQueue;
import org.eclipse.jetty.websocket.WebSocket;

/**
 *
 * @author wflores
 */
class QueueChannelGroup extends ChannelGroup 
{
    private LinkedBlockingQueue<Channel.Connection> queue = new LinkedBlockingQueue();
    
    public QueueChannelGroup(String name) {
        super(name); 
    }

    public void addSocket(WebSocket.Connection conn) {
        String connid = new UID().toString(); 
        addSocket(conn, connid); 
    }

    public void addSocket(WebSocket.Connection conn, String connid) {
        queue.add(new Channel.Connection(conn, connid)); 
    }

    public void removeSocket(WebSocket.Connection conn) {
        if (conn == null) return;
        
        List removelist = new ArrayList();
        for (Channel.Connection info: queue) { 
            if (info.accept(conn)) removelist.add(info); 
        } 
        queue.removeAll(removelist); 
        removelist.clear(); 
    }
    
    public void send(MessageObject msgobj) {
        Channel.Connection info = queue.poll(); 
        if (info == null) return;
        
        try {
            info.send(msgobj); 
        } catch (IOException ex) {
            ex.printStackTrace(); 
        } finally {
            //send back to pool for reuse
            queue.add( info );
        }
    } 

    public void close(int status, String msg) {
        List removelist = new ArrayList();
        Channel.Connection info = null;
        while ((info=queue.poll()) != null) {
            info.close(status, msg); 
            removelist.add(info); 
        } 
        queue.removeAll( removelist ); 
        removelist.clear(); 
    } 
    
    public boolean isEmpty() { 
        return queue.isEmpty(); 
    }     
}
