/*
 * TopicChannel.java
 *
 * Created on March 3, 2013, 10:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import com.rameses.util.MessageObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.websocket.WebSocket;

/**
 *
 * @author Elmo
 */
public class TopicChannel extends Channel 
{    
    private Hashtable<String,ChannelGroup> groups = new Hashtable();
        
    public TopicChannel(String name) {
        this(name, null);
    }
    
    public TopicChannel(String name, Map conf) {
        super(name, conf);
    }    
    
    public synchronized ChannelGroup addGroup(String name) {
        if ( name == null ) {
            name = "default";
        }
        
        String keyname = name.toLowerCase();
        ChannelGroup grp = groups.get(keyname); 
        if ( grp == null ) {
            grp = new TopicChannelGroup(keyname); 
            groups.put(keyname, grp); 
        } 
        return grp; 
    } 
    
    public ChannelGroup getGroup(String name) {
        if (name == null) {
            name = "default";
        }
        return groups.get(name.toLowerCase());
    }    
    
    public String[] getGroupNames() {
        return groups.keySet().toArray(new String[]{}); 
    } 
    
    public void send(MessageObject mo) {
        Iterator<ChannelGroup> items = groups.values().iterator(); 
        while (items.hasNext()) {
            ChannelGroup cg = items.next(); 
            if (cg != null) cg.send(mo); 
        }
    }
    
    //closes all connections
    public void close(int status, String msg ) {
        Iterator<ChannelGroup> items = groups.values().iterator(); 
        while (items.hasNext()) {
            ChannelGroup cg = items.next(); 
            if (cg != null) cg.close(status, msg); 
        }
    }

    public void removeSocket(WebSocket.Connection conn) { 
        ArrayList<ChannelGroup> removals = new ArrayList();
        Iterator<ChannelGroup> items = groups.values().iterator(); 
        while (items.hasNext()) {
            ChannelGroup cg = items.next(); 
            if (cg == null) { continue; }
            
            cg.removeSocket(conn); 
            if ( cg.isEmpty() ) {
                removals.add( cg ); 
            } 
        } 
        
        while ( !removals.isEmpty() ) {
            ChannelGroup cg = removals.remove(0); 
            if ( cg.isEmpty() ) { 
                try { 
                    groups.remove( cg.getName() ); 
                } catch(Throwable t) {;} 
            } 
        }
    }
}
