/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.websocket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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
public class ViewSocketConnectionServlet extends AbstractServlet {
    
    private SocketConnections conns;
    
    public ViewSocketConnectionServlet(SocketConnections conns) {
        this.conns = conns;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
        String taskid = getClass().getName();
        TaskProcess atask = (TaskProcess) req.getAttribute(taskid);
        if ( atask == null ) {
            Map params = buildParams(req);
            String pathInfo = req.getPathInfo();
            if ( pathInfo != null ) {
                params.put("channel", pathInfo); 
            }
            
            Continuation cont = ContinuationSupport.getContinuation(req);
            if ( cont.isInitial() ) {
                cont.setTimeout(getBlockingTimeout());
                cont.suspend();
            }

            atask = new TaskProcess(cont, params); 
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
        private Map request; 
        
        private String channelName; 

        private Object result;
        private int resultMode;
        
        TaskProcess(Continuation cont, Map request) {
            this.cont = cont; 
            this.request = request; 
            
            channelName = (String) request.get("channel"); 
            if ( channelName != null ) {
                channelName = channelName.substring(1).trim(); 
            }
            if ( channelName != null && channelName.length()==0 ) {
                channelName = null; 
            }
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
                StringBuilder sb = new StringBuilder();
                if ( channelName == null ) { 
                    sb.append("\n------------------- ");
                    sb.append("\nAvailable Channels : ");
                    sb.append("\n------------------- ");
                    for ( Channel o : conns.getChannels() ) {
                        sb.append("\n-  ").append( o.getName() ); 
                    } 
                } else { 
                    Channel channel = conns.getChannel( channelName ); 
                    sb.append("\nChannel Name : ").append(channelName);
                    sb.append("\n");
                    sb.append("\nAvailable Groups : "); 
                    for (String sname : channel.getGroupNames()) { 
                        sb.append("\n-  ").append( sname ); 
                    } 
                } 
                result = sb.toString(); 
            } catch(Throwable t) { 
                StringWriter buffer = new StringWriter();
                t.printStackTrace(new PrintWriter(buffer)); 
                result = buffer.toString(); 
                resultMode = 1; 
            } finally {
                cont.resume();
            }
        }
        
        void writeResponse( HttpServletResponse resp, Object value ) {
            PrintWriter out = null;
            try {
                resp.setContentType("text/plain"); 
                out = resp.getWriter(); 
                out.println( result ); 
            } catch(IOException ie) {
                ie.printStackTrace();; 
            } finally { 
                try { out.close(); } catch (Throwable t) {;}
            } 
        } 
    }
    
    // </editor-fold>    
}
