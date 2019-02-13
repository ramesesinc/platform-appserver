
package com.rameses.osiris3.server;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.server.common.AbstractServlet;
import com.rameses.osiris3.xconnection.XAsyncConnection;
import com.rameses.osiris3.xconnection.XConnection;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
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
 * @author wflores
 */
public class AsyncTracertServlet extends AbstractServlet 
{
    private static ExecutorService taskPool;
    
    public void init() throws ServletException {
        taskPool = Executors.newFixedThreadPool(getTaskPoolSize());
    }            
    
    public String getMapping() {
        return "/async/tracert";
    }

    public long getBlockingTimeout() {
        return 60000;
    }

    public int getTaskPoolSize() {
        return 100; 
    }     
    
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {   
        try {
            doServiceImpl(req, resp); 
        } catch(Throwable t) {
            CustomWriter cw = new CustomWriter();
            t.printStackTrace( new PrintWriter(cw) ); 
            System.err.println( cw.getText() ); 
            writeResp( resp, cw.getText() );
        } 
    }
    
    private void doServiceImpl(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {   
        String reqid = AsyncTracertServlet.class.getName();
        TracertTask atask = (TracertTask) req.getAttribute(reqid);
        if (atask == null) {
            Map params = readReq( req ); 
            
            String sval = (String) params.get("context");
            if (sval == null || sval.length() == 0) {
                throw new ServletException("Please specify context parameter"); 
            }
            
            sval = (String) params.get("connection");
            if (sval == null || sval.length() == 0) {
                params.put("connection", "async"); 
            }

            Continuation cont = ContinuationSupport.getContinuation(req);
            if( cont.isInitial() ) {
                cont.setTimeout( getBlockingTimeout()  );
                cont.suspend();
            }
            
            atask = new TracertTask(cont, params); 
            req.setAttribute(reqid, atask);            
            atask.setFuture( taskPool.submit(atask) ); 
            
        } else {
            Object result = null; 
            if (atask.isExpired()) {
                atask.cancel();
                result = new Exception("Timeout exception. Transaction was not processed");
            } else {
                result = atask.getResult();
            }
            
            writeResp( resp, result ); 
        }
    } 

    private Map readReq( HttpServletRequest req ) {
        
        try { 
            Object[] arr = readRequest( req ); 
            return (Map) arr[0]; 
        } catch(Throwable t) {
            //do nothing 
        } 
        
        Map map = new HashMap();
        Enumeration en = req.getParameterNames();
        while (en.hasMoreElements()) { 
            String sname = en.nextElement()+""; 
            Object value = req.getParameter(sname); 
            map.put( sname, value ); 
        } 
        return map;
    }
    
    private void writeResp( HttpServletResponse resp, Object content ) {
        
        String result = null; 
        if (content instanceof Throwable) {
            CustomWriter cw = new CustomWriter();
            ((Throwable)content).printStackTrace( new PrintWriter(cw) ); 
            result = cw.getText(); 
        } else {
            result = content+"";
        }
        
        BufferedOutputStream bos = null; 
        try {
            resp.setContentType( "text/plain" );
            bos = new BufferedOutputStream(resp.getOutputStream());
            bos.write( result.getBytes() );
            
        } catch(Throwable t) {
            t.printStackTrace(); 
        } finally { 
            try { bos.flush(); }catch(Throwable t){;} 
            try { bos.close(); }catch(Throwable t){;} 
        }
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" TracertTask ">
    
    private class TracertTask implements Runnable 
    {
        private Continuation cont;
        private Object result;        
        private Future future; 
        private Map params;

        private String id;
        private String context; 
        private String connection;
        
        TracertTask(Continuation cont, Map params) {
            this.cont = cont; 
            this.params = params; 
            this.result = new StringBuilder();
            
            context = (String) params.get("context");
            if (context == null) context = "default";
            
            connection = (String) params.get("connection");
            if (connection == null) connection = "async"; 
        }
        
        Continuation getContinuation() { 
            return cont;
        }
        
        void setFuture(Future future) {
            this.future = future; 
        }
        
        Object getResult() { 
            return result; 
        }
        
        boolean isExpired() {
            return cont.isExpired(); 
        }
        
        void cancel() {
            if (future == null) { return; }
            
            try { 
                future.cancel(true); 
            } catch(Throwable t) {;} 
        }
        
        public void run() {
            StringBuilder buffer = new StringBuilder();
            try {
                AppContext ctx = OsirisServer.getInstance().getContext( AppContext.class, context );
                XAsyncConnection ac = (XAsyncConnection) ctx.getResource(XConnection.class, connection );
                if (ac == null) {
                    throw new Exception("async connection '"+ connection +"' not found");
                }
                
                ac.trace( buffer );
                
            } catch (Throwable t) { 
                CustomWriter cw = new CustomWriter(); 
                t.printStackTrace(new PrintWriter(cw));
                
                buffer.append("\n   Status: ERROR"); 
                buffer.append("\n" + cw.getText() ); 
                
            } finally {
                result = buffer.toString();  
                cont.resume(); 
            }
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" CustomWriter (Class) ">
    
    private class CustomWriter extends Writer
    {
        StringBuilder buffer = new StringBuilder();
        
        public void write(char[] cbuf, int off, int len) throws IOException {
            buffer.append(cbuf, off, len);
        }

        public String getText() { return buffer.toString(); }
        
        public void flush() throws IOException {;}
        public void close() throws IOException {;}
    }
    
    // </editor-fold>
} 
