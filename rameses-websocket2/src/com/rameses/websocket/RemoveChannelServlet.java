/*
 * AddChannelServlet.java
 *
 * Created on May 18, 2013, 3:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

/**
 *
 * @author Elmo
 * modified-by: wflores
 */
public class RemoveChannelServlet extends AbstractServlet {
    
    private SocketConnections sockets;
    
    public RemoveChannelServlet(SocketConnections sockets) {
        this.sockets = sockets;
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        String taskid = getClass().getName();
        TaskProcess atask = (TaskProcess) req.getAttribute(taskid);
        if (atask == null) {
            Object o = readRequest(req); 
            if (o instanceof Exception) {
                writeResponse(resp, o); 
                return; 
            }
            
            Collection list = null;
            if (o instanceof Collection) { 
                list = (Collection) o; 
            } else if (o instanceof Object[]) { 
                list = Arrays.asList((Object[]) o);
            } else {
                list = new ArrayList();
                if ( o != null ) {
                    list.add( o );
                } 
            } 
            
            Continuation cont = ContinuationSupport.getContinuation(req);
            if( cont.isInitial() ) {
                cont.setTimeout(getBlockingTimeout());
                cont.suspend();
            }

            atask = new TaskProcess(cont, list); 
            req.setAttribute(taskid, atask);            
            atask.future = submit(atask); 
        } else {
            Object result = null; 
            if (atask.isExpired()) {
                atask.cancel();
                result = new Exception("Timeout exception. Transaction was not processed");
            } else {
                result = atask.result; 
            }
            writeResponse(resp, result);
        }     
    } 
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String taskid = getClass().getName();
        TaskProcess atask = (TaskProcess) req.getAttribute(taskid);
        if (atask == null) {
            List list = new ArrayList();
            list.add(buildParams(req));
            
            Continuation cont = ContinuationSupport.getContinuation(req);
            if( cont.isInitial() ) {
                cont.setTimeout(getBlockingTimeout());
                cont.suspend();
            }

            atask = new TaskProcess(cont, list); 
            req.setAttribute(taskid, atask);            
            atask.future = submit(atask); 
        } else {
            Object result = null; 
            if (atask.isExpired()) {
                atask.cancel();
                result = new Exception("Timeout exception. Transaction was not processed");
            } else {
                result = atask.result; 
            }
            writeResponse(resp, result);
        } 
    } 
        
    private void removeChannel( String name ) {
        sockets.removeChannel(name); 
        System.out.println("channel "+ name +" removed");
    }
    
    // <editor-fold defaultstate="collapsed" desc=" TaskProcess ">
    
    private class TaskProcess implements Runnable {
        private Continuation cont;
        private Collection list;
        private Future future;
        private Object result;
        
        TaskProcess(Continuation cont, Collection list) {
            this.cont = cont; 
            this.list = list; 
        }
        
        boolean isExpired() {
            return (cont == null || cont.isExpired()); 
        }
        
        void cancel() {
            try { 
                if (future != null) {
                    future.cancel(true);
                } 
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
                
        @Override
        public void run() {
            try {
                Iterator itr = list.iterator(); 
                while (itr.hasNext()) { 
                    Object obj = itr.next(); 
                    if (!( obj instanceof Map )) { 
                        continue; 
                    } 
                    
                    Map conf = (Map) obj;                       
                    removeChannel((String) conf.get("channel"));  
                } 
                list.clear(); 
                result = "OK";
            } catch(Exception e) {
                result = e; 
            } catch(Throwable t) {
                result = new Exception(t.getMessage(), t); 
            } finally {
                cont.resume();
                cont = null;
            }
        }
    }
    
    // </editor-fold>       
}
