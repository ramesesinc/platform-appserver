/*
 * QueueMessageHandler.java
 *
 * Created on February 7, 2013, 11:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import com.rameses.common.ExpressionResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Elmo
 */
public class QueueMessageHandler implements MessageHandler {
    
    private LinkedBlockingQueue queue = new LinkedBlockingQueue();
    private String id;
    private String evalExpr;
    
    //timeout in seconds
    private long timeout = 30000;
    
    public QueueMessageHandler(String id, String evalExpr) {
        this.id = id;
        this.evalExpr = evalExpr;
    }
    
    public void onMessage(Object data) {
        queue.add( data );
    }
    
    public String getId() {
        return id;
    }
    
    //this is the blocking command
    public Object getMessage() throws Exception {
        return queue.poll( timeout, TimeUnit.MILLISECONDS );
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    public int hashCode() {
        return this.id.hashCode();
    }
    
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }
    
    public boolean accept(Object data) 
    {
        boolean accept = true;
        if( evalExpr!=null && evalExpr.trim().length()>0  ) 
        {
            Map map = new HashMap();
            map.put("data", data);
            
            try {
                accept = ExpressionResolver.getInstance().evalBoolean(evalExpr, map);
            } catch(Exception ign){;}            
        }
        return accept;
    }
    
}
