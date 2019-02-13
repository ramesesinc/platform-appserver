/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.websocket;

import com.rameses.util.SealedMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wflores
 */
public abstract class AbstractServlet extends HttpServlet 
{
    private final static Object LOCK = new Object();    
    private final static long BLOCKING_TIMEOUT = 30000;
    private final static int TASK_POOL_SIZE = 100;
    
    private static ExecutorService taskPool;

    public long getBlockingTimeout() {
        return BLOCKING_TIMEOUT; 
    }
    
    public int getTaskPoolSize() {
        return TASK_POOL_SIZE; 
    }
    
    protected ExecutorService getTaskPool() {
        synchronized (LOCK) {
            if (taskPool == null) {
                taskPool = Executors.newFixedThreadPool(getTaskPoolSize());
            } 
            return taskPool; 
        }
    }
    
    protected Future submit(Runnable task) {
        return getTaskPool().submit(task); 
    }
    
    protected Map buildParams(HttpServletRequest hreq) {
        Map params = new HashMap();
        Enumeration e = hreq.getParameterNames();
        while(e.hasMoreElements()) {
            String name = (String)e.nextElement();
            params.put( name, hreq.getParameter(name) );
        }
        return params;
    } 
    
    protected Object readRequest(HttpServletRequest hreq) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(hreq.getInputStream());

            Object o = in.readObject();
            if (o instanceof SealedMessage) { 
                o = ((SealedMessage)o).getMessage(); 
            } 
            return o; 
        } catch(Exception e) { 
            return e; 
        } catch(Throwable t) {
            return new Exception(t.getMessage(), t); 
        } finally {
            try { in.close(); } catch(Throwable t){;}
        }        
    }
    
    protected void writeResponse(HttpServletResponse resp, Object value) {
        ObjectOutputStream out = null; 
        try {
            out = new ObjectOutputStream(resp.getOutputStream());
            if (value instanceof Throwable) {
                WriterImpl impl = new WriterImpl();
                ((Throwable) value).printStackTrace(new PrintWriter(impl)); 
                out.writeObject(impl.getText()); 
            } else {
                out.writeObject(value+""); 
            } 
        } catch(Throwable t) {
            t.printStackTrace(); 
        } finally {
            try { out.close(); }catch(Throwable t){;} 
        }
    }
    
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
