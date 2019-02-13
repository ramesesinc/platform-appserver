/*
 * SocketConnections.java
 *
 * Created on December 28, 2012, 10:30 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.mq;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class SocketConnections {
    
    private final Map<String, Session> sessions = Collections.synchronizedMap(new HashMap());
    
    /** Creates a new instance of SocketConnections */
    public SocketConnections() {
       
    }
    
    public void sendMessage(String sessionid, String message) {
        Session session = getSession(sessionid);
        session.sendMessage( message );
    }
    
    public Session addSession(String id) {
        if( !sessions.containsKey(id) ) {
            Session s = new Session(id);
            sessions.put(id, s);
            return s;
        }
        else {
            return sessions.get(id);
        }
    }
    
    //reconnect non existent connection 
    public Session getSession(String id) {
        if(!sessions.containsKey(id)) {
             addSession(id);
        }
        return sessions.get(id);
    }
    
    public void removeSession(String id) {
        Session s = sessions.remove(id);
        if(s!=null) s.close();
    }
    
    public void shutdown() {
        for(Session s: sessions.values() ) {
            s.close();
        }
        sessions.clear();
    }
}
