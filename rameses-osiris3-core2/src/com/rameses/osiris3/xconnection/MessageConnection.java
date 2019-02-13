/*
 * MessageConnection.java
 *
 * Created on February 25, 2013, 10:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author Elmo
 */
public abstract class MessageConnection extends XConnection {
    
    private Set<MessageHandler> handlers =  new CopyOnWriteArraySet();
    
    public abstract void send(Object data);
    public abstract void sendText(String data);
    public abstract void send(Object data, String queueName);
    //this is used for handling direct responses
    public abstract void addResponseHandler(String tokenid, MessageHandler handler) throws Exception;
    
    public void removeQueue(String name){
        //do nothing...
    }
    
    public void stop() {
        handlers.clear();
    }
    
    public synchronized void addHandler( MessageHandler mh) {
        handlers.add( mh );
    }
    
    //this is usually called OnMessage event
    public synchronized void notifyHandlers(Object data) {
        for( MessageHandler mh: handlers ) {
            if(mh.accept(data)) {
                mh.onMessage( data );
            }
        }
    }
    
}
