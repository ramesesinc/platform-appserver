/*
 * ServiceInvokerServlet.java
 *
 * Created on January 10, 2013, 2:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;


import com.rameses.common.AsyncException;
import com.rameses.common.AsyncRequest;
import com.rameses.common.AsyncToken;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.script.RemoteScriptRunnable;
import com.rameses.osiris3.script.ScriptRunnable;
import com.rameses.osiris3.script.ScriptRunnableListener;
import com.rameses.osiris3.server.common.AbstractServlet;
import com.rameses.osiris3.xconnection.MessageQueue;
import com.rameses.osiris3.xconnection.XAsyncConnection;
import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.server.ServerPID;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
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
 *
 * Pattern for invoking is :
 * http://<server>:<port>/osiris3/<app.context>/<service>.<method>
 *
 * for asking info:
 * http://<server>:<port>/osiris3-info/<app.context>/<service>
 */

public class ServiceInvokerServlet extends AbstractServlet {
    
    private static ExecutorService taskPool;
    
    public String getMapping() {
        return "/services/*";
    }
    
    
    public void init() throws ServletException {
        taskPool = Executors.newFixedThreadPool(getTaskPoolSize());
    }
    
    private class ContinuationListener extends ScriptRunnableListener {
        private Continuation continuation;
        private Future future;
        private HttpServletRequest req;
        
        //added read timeout variable 
        int read_timeout_var = 0; 
        
        public ContinuationListener(HttpServletRequest req) {
            this.req = req;
        }
        public void start() {
            continuation = ContinuationSupport.getContinuation(req);
            if( continuation.isInitial() ) {
                if (read_timeout_var > 0) {
                    continuation.setTimeout(read_timeout_var); 
                } else {
                    continuation.setTimeout( getBlockingTimeout() );
                }
                continuation.suspend();
            }
        }
        public void onClose() {
            if(continuation!=null) {
                continuation.resume();
                continuation = null;
            }
        }
        
        public void onCancel() {
            future.cancel(true);
        }
        
        public boolean isExpired() {
            return (continuation!=null && continuation.isExpired());
        }
    }
    
    protected void service(final HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!ServerPID.isCleared()) {
            Object o = new Exception("Server is initializing please wait.");
            writeResponse( o, res );
            return;
        }
        RequestParser p = new RequestParser(req);
        processBasicService( p, req, res );
    }
    
    private void processBasicService(RequestParser p, final HttpServletRequest req, HttpServletResponse res)  throws ServletException, IOException{
        ScriptRunnable tr = (ScriptRunnable) req.getAttribute( ScriptRunnable.class.getName() );
        if(tr==null) {
            ContinuationListener listener = new ContinuationListener(req);
            //replace the values
            Object[] params = readRequest(req);
            
            AppContext ct = OsirisServer.getInstance().getContext( AppContext.class, p.getContextName() );
            String serviceName = p.getServiceName();
            int idx = ( serviceName == null ? 0 : serviceName.indexOf(':'));
            if ( idx <= 0 ) {
                //process normal scripts
                tr = new ScriptRunnable( (MainContext)ct );
                tr.setServiceName( serviceName );
            }
            else {
                //handle remote scripts separately
                String tokenid = null;
                if ( params.length > 0 && params[0] instanceof Map ) {
                    tokenid = (String) ((Map) params[0]).get("tokenid");
                } 
                else if (params[0] instanceof Object[]) {
                    Object[] arr = (Object[]) params[0]; 
                    if ( arr.length > 0 && arr[0] instanceof Map) {
                        tokenid = (String) ((Map) arr[0]).get("tokenid");
                    }
                }
                RemoteScriptRunnable rem  =new RemoteScriptRunnable( (MainContext)ct);
                rem.setHostName( serviceName.substring(0, idx)); 
                rem.setServiceName( serviceName.substring(idx+1)); 
                rem.setTokenid( tokenid );
                tr = rem;
            }
            
            tr.setBypassAsync(false);
            tr.setMethodName(p.getMethodName());
            if ( params[0] == null ) {
                tr.setArgs(new Object[]{});
            } else {
                tr.setArgs((Object[])params[0] );
            }
            tr.setEnv( (Map)params[1] );
            tr.setListener( listener );
            try {
                Integer rt = new Integer(tr.getEnv().get("@read_timeout").toString()); 
                listener.read_timeout_var = rt.intValue(); 
            } catch(Throwable t){;} 
            
            listener.start();
            req.setAttribute(  ScriptRunnable.class.getName(), tr );
            listener.future = taskPool.submit(tr);
            
        } else {
            ContinuationListener listener = (ContinuationListener)tr.getListener();
            Object response= null;
            if( listener.isExpired() ) {
                tr.cancel();
                response = new Exception("Timeout exception. Transaction was not processed");
                
            } else {
                if( tr.hasErrs()) {
                    response = tr.getErr();
                    System.out.println("error "+tr.getErr().getClass() + " " + tr.getErr().getMessage());
                    
                } else {
                    response = tr.getResult();
                    if(response instanceof AsyncRequest) {
                        AsyncRequest ar = (AsyncRequest)response;
                        ar.setContextName( p.getContextName());
                        
                        XAsyncConnection ac = null; 
                        if( ar.getConnection() != null) {
                            try {
                                ac = (XAsyncConnection) tr.getContext().getResource( XConnection.class, ar.getConnection() );
                                ac.register( ar.getId() );
                            } catch(Exception e) {
                                writeResponse( e, res );
                                return; 
                            }
                        }
                        
                        try {
                            tr.setBypassAsync(true);
                            tr.setAsyncRequest(ar);
                            tr.setListener(new AsyncListener(tr, ac));
                            taskPool.submit( tr );
                        } catch(Exception e) {
                            writeResponse( e, res );
                            return; 
                        }
                        
                        response = new AsyncToken(ar.getId(), ar.getConnection());
                    }
                }
            }
            writeResponse( response, res );
        }
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" AsyncListener ">
    
    private class AsyncListener extends ScriptRunnableListener {
        private ScriptRunnable sr;
        private XAsyncConnection conn;
        
        public AsyncListener(ScriptRunnable sr, XAsyncConnection conn) {
            this.sr = sr; 
            this.conn = conn;
        }
        
        public void onBegin() {}
        public void onClose() {}
        public void onCancel() {}
        public void onRollback(Exception e) {
            if (conn != null) {
                taskPool.submit(new ErrorHandler(e));
            }
        } 
        
        public void onComplete(Object result) {
            if (conn == null) {
               //do nothing, exit immediately
                
            } else {
                try {
                    AsyncRequest ar = sr.getAsyncRequest();
                    boolean hasmore = "true".equals(sr.getEnv().get(ar.getVarStatus())+""); 
                    MessageQueue queue = conn.getQueue( ar.getId() );
                    queue.push( result ); 
                    if (hasmore) { 
                        ar.getEnv().put(ar.getVarStatus(), null); 
                        taskPool.submit( sr ); 
                    } else { 
                        AsyncToken at = new AsyncToken();
                        at.setClosed(true);
                        queue.push( at);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                } 
            } 
        } 
        
        class ErrorHandler implements Runnable {
            private Exception e;
            
            ErrorHandler(Exception e) {
                this.e = e; 
            }
            
            AsyncException resolve(Throwable e) {
                if (e.getClass().getName().indexOf(".groovy.") > 1) {
                    Throwable t = e.getCause(); 
                    return resolve(t); 
                }
                
                WriterImpl wi = new WriterImpl();
                e.printStackTrace(new PrintWriter(wi)); 
                return new AsyncException(e.getMessage(), new Exception(wi.getText())); 
            } 
            
            public void run() {
                try {
                    if (e == null) return;
                    
                    AsyncRequest ar = sr.getAsyncRequest();
                    MessageQueue queue = conn.getQueue( ar.getId() );
                    AsyncToken at = new AsyncToken();
                    at.setClosed(true);
                    
                    queue.push(resolve(e)); 
                    queue.push(at);
                } catch(Throwable t) {
                    t.printStackTrace();
                } 
            }
        }
    }
    
    // </editor-fold>
 
    // <editor-fold defaultstate="collapsed" desc=" WriterImpl ">
    
    private class WriterImpl extends Writer{
        StringBuffer buffer = new StringBuffer();
        
        public void write(char[] cbuf, int off, int len) throws IOException {
            buffer.append(cbuf, off, len);
        }

        public String getText() { return buffer.toString(); }
        
        public void flush() throws IOException {;}
        public void close() throws IOException {;}
    }
    
    // </editor-fold>   
    
}
