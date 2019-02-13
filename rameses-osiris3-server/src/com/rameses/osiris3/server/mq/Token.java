/*
 * Token.java
 *
 * Created on January 3, 2013, 7:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.mq;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import org.eclipse.jetty.continuation.Continuation;

/**
 *
 * @author Elmo
 */
public class Token {
    
    
    private String id;
    private Queue<String> queue = new LinkedList();
    private Continuation continuation;
    private Date expiryDate = new Date();
    private Session session;
    
    /** Creates a new instance of SessionToken */
    public Token(String id, Session s) {
        this.id = id;
        this.session = s;
    }
    
    public String getMessage() {
        return queue.poll();
    }
    
    public boolean hasMessages() {
        return queue.size()>0;
    }
    
    public void setContinuation(Continuation continuation) {
        this.continuation = continuation;
    }
    
    public void send(String msg) throws Exception {
        queue.add(msg);
        // wakeup member if polling
        if (continuation!=null) {
            continuation.resume();
            continuation=null;
        }
    }
    
    public void doClose() throws Exception {
        send( "{status: 'closed'}");
    }
    
    public String getId() {
        return id;
    }
    
    public Continuation getContinuation() {
        return continuation;
    }
    
    //expiry date allowance is 1 minute. After this time it is considered dead
    public void updateExpiry() {
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.SECOND, 60000 );
        expiryDate = cal.getTime();
    }
    
    public boolean isExpired() {
        return expiryDate.before(new Date());
    }

    public Session getSession() {
        return session;
    }
    
}
