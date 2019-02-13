/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.websocket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.Future;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

/**
 *
 * @author wflores 
 */
public class ManageSocketConnectionsServlet extends AbstractServlet {
    
    private SocketConnections sockets;
    
    public ManageSocketConnectionsServlet(SocketConnections sockets) {
        this.sockets = sockets;
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String taskid = getClass().getName();
        TaskProcess atask = (TaskProcess) req.getAttribute(taskid);
        if (atask == null) {
            Object o = readRequest(req); 
            if (o instanceof Exception) {
                writeResponse(resp, o); 
                return; 
            }
            
            Continuation cont = ContinuationSupport.getContinuation(req);
            if ( cont.isInitial() ) {
                cont.setTimeout(getBlockingTimeout());
                cont.suspend();
            }

            atask = new TaskProcess(cont, (Map)o); 
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
            atask.writeResponse( resp, result );
        }
    }    
 
    // <editor-fold defaultstate="collapsed" desc=" TaskProcess ">
    
    private class TaskProcess implements Runnable {
        private Continuation cont;
        private Future future;
        private Object result;
        private Map request; 
        
        TaskProcess(Continuation cont, Map request) {
            this.cont = cont; 
            this.request = request; 
        }
        
        boolean isExpired() { 
            if ( cont == null ) {
                return true; 
            } 
            
            boolean expired = cont.isExpired(); 
            if ( expired ) { 
                cont = null; 
            }
            return expired; 
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
                
        public void run() {
            try {
                String method = (String) request.get("method"); 
                if ( "exist".equals( method )) { 
                    result = sockets.exist(getString(request,"channel"), getString(request,"channelgroup"));
                } else if ("getChannelGroups".equals(method)) {
                    result = sockets.getChannelGroups(); 
                } else {
                    result = new Exception("'"+ method + "' method not found");
                }
            } catch(Exception e) {
                result = e; 
            } catch(Throwable t) {
                result = new Exception(t.getMessage(), t); 
            } finally {
                cont.resume();
            }
        }
        
        void writeResponse( HttpServletResponse resp, Object value ) {
            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(resp.getOutputStream());
                out.writeObject( value ); 
            } catch (Exception ex) { 
                try {
                    out = new ObjectOutputStream(resp.getOutputStream());
                    out.writeObject( ex ); 
                } catch(Throwable t){;} 
            } finally { 
                try { out.close(); } catch (Throwable t) {;}
            }
        }
    }
    
    private String getString( Map conf, Object key ) {
        Object val = (conf == null ? null: conf.get(key)); 
        return (String) val; 
    }
    private Number getNumber( Map conf, Object key ) {
        Object val = (conf == null ? null: conf.get(key)); 
        if ( val == null ) {
            return null; 
        } else if (val instanceof Number) {
            return (Number)val;
        } else {
            return new Integer( val.toString() ); 
        } 
    }     
    
    // </editor-fold>
}
