/*
 * AsyncResponseServlet.java
 *
 * Created on May 29, 2014, 5:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.common.AsyncBatchResult;
import com.rameses.common.AsyncToken;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.server.common.AbstractServlet;
import com.rameses.osiris3.xconnection.MessageQueue;
import com.rameses.osiris3.xconnection.XAsyncConnection;
import com.rameses.osiris3.xconnection.XConnection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

/**
 *
 * @author Elmo
 */
public class AsyncResponseServlet extends AbstractServlet 
{
    private static ExecutorService taskPool;
    
    public void init() throws ServletException {
        taskPool = Executors.newFixedThreadPool(getTaskPoolSize());
    }            
    
    public String getMapping() {
        return "/async/poll";
    }

    public long getBlockingTimeout() {
        return 30000;
    }

    public int getTaskPoolSize() {
        return 100; 
    }
    
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {        
        String reqid = AsyncResponseServlet.class.getName();
        PollTask atask = (PollTask) req.getAttribute(reqid);
        if (atask == null) {
            Object[] args = readRequest(req); 
            Object arg0 = (args.length > 0? args[0]: null);    
            List items = new ArrayList();
            if (arg0 instanceof List) {
                items = (List)arg0; 
            } else if (arg0 instanceof Object[]) {
                items = Arrays.asList((Object[]) arg0);
            } else { 
                items.add(arg0); 
            } 
            
            Map params = (items.isEmpty()? new HashMap(): (Map)items.get(0)); 
            String id = (String) params.get("id");
            if (id == null || id.length() == 0) 
                throw new ServletException("Please specify id");

            Continuation cont = ContinuationSupport.getContinuation(req);
            if( cont.isInitial() ) {
                cont.setTimeout( getBlockingTimeout()  );
                cont.suspend();
            }
            
            atask = new PollTask(cont, params); 
            req.setAttribute(reqid, atask);            
            Future future = taskPool.submit(atask); 
            atask.setFuture(future); 
        } else {
            Object result = null; 
            if (atask.isExpired()) {
                atask.cancel();
                result = new Exception("Timeout exception. Transaction was not processed");
            } else {
                result = atask.getResult();
            }
            writeResponse(result, resp); 
        }
    } 

    
    // <editor-fold defaultstate="collapsed" desc=" PollTask ">
    
    private class PollTask implements Runnable 
    {
        private Continuation cont;
        private Future future; 
        private Map params;
        private Object result;

        private String id;
        private String context; 
        private String connection;
        private Object token;
        
        PollTask(Continuation cont, Map params) {
            this.cont = cont; 
            this.params = params; 
            this.id = params.get("id").toString();
            
            context = (String) params.get("context");
            if (context == null) context = "default";
            
            connection = (String) params.get("connection");
            if (connection == null) connection = "async"; 
            
            token = params.get("token"); 
        }
        
        Continuation getContinuation() { 
            return cont;
        }
        
        void setFuture(Future future) {
            this.future = future; 
        }
        
        Object getResult() { return result; }
        
        boolean isExpired() {
            return cont.isExpired(); 
        }
        
        void cancel() {
            if (future != null) future.cancel(true); 
        }
        
        public void run() {
            try {
                AppContext ctx = OsirisServer.getInstance().getContext( AppContext.class, context );
                XAsyncConnection ac = (XAsyncConnection) ctx.getResource(XConnection.class, connection );
                if (ac == null) throw new Exception("async connection '"+ connection +"' not found");
                
                MessageQueue queue = ac.getQueue( id ); 
                result = queue.poll(); 
                if (result instanceof AsyncBatchResult) {
                    AsyncBatchResult batch = (AsyncBatchResult)result; 
                    if (batch.hasEOF()) ac.unregister(id); 
                } else if (result instanceof AsyncToken) {
                    AsyncToken at = (AsyncToken)result; 
                    if (at.isClosed()) ac.unregister(id); 
                } 
            } catch (Exception ex) {
                result = ex; 
            } finally {
                cont.resume(); 
            }
        } 
    }
    
    // </editor-fold>
    
} 
