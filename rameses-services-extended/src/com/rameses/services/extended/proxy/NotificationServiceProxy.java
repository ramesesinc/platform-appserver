/*
 * WrokflowServiceProxy.java
 *
 * Created on June 27, 2014, 5:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.services.extended.proxy;

import com.rameses.common.MethodResolver;
import com.rameses.util.ExceptionManager;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class NotificationServiceProxy {
    
    private Object svc;
    
    /** Creates a new instance of WrokflowServiceProxy */
    public NotificationServiceProxy(Object w) {
        this.svc = w;
    }

    public void addMessage(Map msg) throws Exception{
        try {
            MethodResolver.getInstance().invoke(svc, "addMessage", new Object[]{msg});
        }
        catch(Exception e) {
            throw ExceptionManager.getOriginal(e);
        }
    }
     
    
}
