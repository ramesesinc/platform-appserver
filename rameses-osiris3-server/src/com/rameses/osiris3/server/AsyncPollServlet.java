/*
 * AsyncPollServlet.java
 *
 * Created on January 21, 2013, 6:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;


import com.rameses.common.AsyncResponse;
import com.rameses.osiris3.cache.CacheConnection;

import com.rameses.osiris3.cache.ChannelNotFoundException;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.OsirisServer;

import com.rameses.osiris3.server.common.AbstractServlet;
import com.rameses.osiris3.xconnection.XConnection;

import java.io.IOException;
import java.util.HashMap;
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
public class AsyncPollServlet extends AbstractServlet {
    
    private final static ExecutorService thread = Executors.newCachedThreadPool();
    private final static long TIMEOUT = 30000;
    
    public String getMapping() {
        return "/poll/*";
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
    
    
    /**
     * extract the path after poll as follows:
     * http://<server>:<port>/<cluster>/poll/<context>/<cache-provider>/<channel>
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        QueuePoll poll = (QueuePoll)req.getAttribute( "QP" );
        if(poll==null) {
            String[] arr = req.getPathInfo().substring(1).split("/");
            String context = arr[0].trim();
            String provider = arr[1].trim();
            String channel = arr[2].trim();
            poll = new QueuePoll(context,provider,channel);
            
            //suspend the continuation
            poll.continuation = ContinuationSupport.getContinuation(req);
            poll.continuation.setTimeout( TIMEOUT );
            poll.timeout = TIMEOUT;
            poll.continuation.suspend();
            poll.future = thread.submit( poll );
            req.setAttribute( "QP", poll );
        }
        else {
            req.removeAttribute( "QP" );
            if( poll.continuation.isExpired()) {
                poll.future.cancel(true);
                poll.asyncResult.setStatus( AsyncResponse.TIMEOUT );
                writeResponse( poll.asyncResult, resp );
            }
            else {
                writeResponse( poll.asyncResult, resp );
            }
        }
    }

    @Override
    protected void writeResponse( Object result, HttpServletResponse resp ) {
        super.writeResponse( result , resp);
    }
    
    private class QueuePoll implements Runnable {
        
        private boolean cancelled;
        private String provider;
        private String channelName;
        private String contextName;
        private String token;
        private Continuation continuation;
        private Future future;
        private long timeout;
        private AsyncResponse asyncResult;
        
        public QueuePoll(String context, String connName, String channel) {
            this.provider = (connName==null) ? "default" : connName;
            this.channelName = channel;
            this.contextName = context;
            this.asyncResult = new AsyncResponse();
        }

        public void run() {
            if(cancelled) return;
            AppContext ctx = OsirisServer.getInstance().getContext( AppContext.class, contextName );
            try {
                CacheConnection cache = (CacheConnection) ctx.getResource( XConnection.class,CacheConnection.CACHE_KEY );

                //we'll have to do a blocking get then remove if the item is already there
                //we do not need to poll to long here to wait for bulk responses.
                Map<String, Object> result = cache.getBulk( channelName, 2 );
                if(result!=null) {
                    for(Object o: result.values()) {
                        asyncResult.addValue( o );
                    }
                }
                asyncResult.setStatus( AsyncResponse.PROCESSING );
            }
            catch(ChannelNotFoundException cnfe) {
                asyncResult.setStatus( AsyncResponse.COMPLETED );
            }
            catch(Exception e) {
                asyncResult.setStatus( AsyncResponse.TIMEOUT );
                
            }
            finally {
                continuation.resume();
            }
        }
    }
    
    private void println(Object value) {
        System.out.println("AsyncPollServlet: " + value); 
    }
}
