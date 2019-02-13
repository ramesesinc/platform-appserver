/*
 * AsyncGetDataServlet.java
 *
 * Created on May 27, 2014, 2:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.osiris3.server.common.AbstractServlet;
import java.io.IOException;
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
 * @author wflores 
 */
public class AsyncGetDataServlet extends AbstractServlet 
{    
    private static ExecutorService taskPool;
    
    public void init() throws ServletException {
        taskPool = Executors.newFixedThreadPool(getTaskPoolSize());
    }            
    
    public String getMapping() {
        return null;
    }

    public long getBlockingTimeout() {
        return 30000;
    }

    public int getTaskPoolSize() {
        return 100; 
    }
    
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {        
        String reqid = AsyncGetDataServlet.class.getName();
        PollTask ptask = (PollTask) req.getAttribute(reqid);
        if (ptask == null) {
            Object[] params = readRequest(req); 
            Map data = (Map)params[0];
            String id = (String) data.get("id");
            if (id == null || id.length() == 0) {
                throw new ServletException("Please specify id");
            } 
                        
            Continuation cont = ContinuationSupport.getContinuation(req);
            if( cont.isInitial() ) {
                cont.setTimeout( getBlockingTimeout()  );
                cont.suspend();
            }

            PollTask task = new PollTask(cont, data);            
            req.setAttribute(reqid, task); 
            Future future = taskPool.submit(task); 
            task.setFuture(future); 
        } else {
            Object result = null; 
            if (ptask.isExpired()) {
                ptask.cancel();
                result = new Exception("Timeout exception. Transaction was not processed");
            } else {
                result = ptask.getResult();
            }
            writeResponse(result, resp); 
        }
    } 

    private class PollTask implements Runnable 
    {
        private Continuation cont;
        private Future future; 
        private Map data;
        private Object result;
        
        PollTask(Continuation cont, Map data) {
            this.cont = cont; 
            this.data = data; 
        }
        
        Continuation getContinuation() { 
            return cont;
        }
        
        Object getResult() {
            return result;
        }
        
        void setFuture(Future future) {
            this.future = future; 
        }
        
        boolean isExpired() {
            return cont.isExpired(); 
        }
        
        void cancel() {
            if (future != null) future.cancel(true); 
        }
        
        public void run() {
            String id = (String) data.get("id");
            try {
                result = AsyncQueue.poll(id);
                cont.resume(); 
            } catch (Exception ex) {
                ex.printStackTrace();
            } 
        } 
    }
}