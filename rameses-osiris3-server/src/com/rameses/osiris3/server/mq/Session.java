/*
 * Session.java
 *
 * Created on January 3, 2013, 7:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.mq;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author Elmo
 */
public class Session {
    
    private String id;
    private Set<Token> tokens = new CopyOnWriteArraySet();
    
    /** Creates a new instance of Session */
    public Session(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public Token add(String id) {
        Token t = new Token( id, this );
        tokens.add(t);
        return t;
    }
    
    public Token findToken(String id ) {
        for(Token t: tokens ) {
            if(t.getId().equals(id)) return t;
        }
        return null;
    }
    
    public void remove(Token t ) {
        tokens.remove(t);
    }
    
    public void close() {
        sendMessage( "{status:'_:ended:'}" );
        tokens.clear();
    }
    
    public void sendMessage(String msg) {
        Set deadTokens = new HashSet();
        for(Token t: tokens) {
            try {
                t.send( msg );
            }
            catch(Exception e){
                deadTokens.add(t);
            }
        }
        tokens.retainAll(deadTokens);
    }
    
}
