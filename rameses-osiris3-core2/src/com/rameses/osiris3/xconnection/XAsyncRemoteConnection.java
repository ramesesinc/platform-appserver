/*
 * XAsyncLocalConnection.java
 *
 * Created on May 27, 2014, 2:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import com.rameses.common.AsyncRequest;
import com.rameses.http.HttpClient;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class XAsyncRemoteConnection extends XConnection implements XAsyncConnection  
{
    private Map conf;
    private String name;
    private String host;    
    private String cluster;
    private String context;
    private String connection;
    private boolean debug;
    private boolean failOnConnectionError;
        
    public XAsyncRemoteConnection(String name, Map conf) {
        this.name = name;
        this.conf = conf;
        this.failOnConnectionError = true;
        
        debug = "true".equals( getProperty("debug", conf)+"" );
        if ("false".equals(getProperty("failOnConnectionError", conf)+"")) {
            failOnConnectionError = false;
        } 
    } 
    
    public void start() {
        if (debug) {
            System.out.println("[" + getClass().getSimpleName() + "_start] " + name);
        } 
        
        host = getProperty("host");
        if (host == null) {
            host = getProperty("app.host");
        }
        
        cluster = getProperty("cluster"); 
        if (cluster == null) {
            cluster = getProperty("app.cluster");
        } 
        if (cluster == null || cluster.trim().length()==0 ) {
            cluster = "osiris3";
        } 
        
        context = getProperty("context");
        if (context == null) {
            context = getProperty("app.context");
        } 
        
        connection = getProperty("connection"); 
    }
    
    public void stop() {
        if (debug) {
            System.out.println("[" + getClass().getSimpleName() + "_stop] " + name);
        }        
    }
    
    public Map getConf() {
        return conf;
    }
    
    public MessageQueue getQueue(String id) throws Exception {
        return new RemoteMessageQueue(id, host, cluster, context);
    }
    
    public MessageQueue register(String id) throws Exception {
        RemoteMessageQueue mq = new RemoteMessageQueue(id, host, cluster, context);
        mq.register(); 
        return mq; 
    }
        
    public void unregister(String id) throws Exception {        
        RemoteMessageQueue mq = new RemoteMessageQueue(id, host, cluster, context);
        mq.unregister(); 
    }
    
    public Object poll(String id) throws Exception {
        RemoteMessageQueue mq = new RemoteMessageQueue(id, host, cluster, context);
        return mq.poll(); 
    }
    
    public void push(String id, Object data) throws Exception {
        RemoteMessageQueue mq = new RemoteMessageQueue(id, host, cluster, context);
        mq.push(data); 
    }
    
    public void submitAsync(AsyncRequest ar) {
    }     

    public void trace( StringBuilder buffer ) {
        new RemoteMessageQueue(null, host, cluster, context).trace( buffer ); 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    private String getProperty( Object key ) {
        return getProperty( key, getConf() ); 
    }
    private String getProperty( Object key, Map conf ) {
        Object value = (conf == null ? null : conf.get(key)); 
        return (String) value; 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" RemoteMessageQueue ">
    
    public class RemoteMessageQueue implements MessageQueue
    {
        XAsyncRemoteConnection root = XAsyncRemoteConnection.this;
        
        private String cluster;
        private String context;
        private String host;
        private String id;
        
        public RemoteMessageQueue(String id, String host, String cluster, String context) {
            this.id = id; 
            this.host = host;             
            this.cluster = cluster;
            this.context = context;
        }
        
        private Object post(String path, Object params) throws Exception {
            try {
                HttpClient client = new HttpClient(host, true);
                return client.post(path, new Object[]{ params }); 
            } catch(Exception e) {
                if (isConnectionError(e) && root.failOnConnectionError) {
                    throw e; 
                } else {
                    e.printStackTrace();
                    return null; 
                }
            }
        } 
            
        private boolean isConnectionError(Exception e) {
            if (e instanceof java.net.ConnectException) return true; 
            else if (e instanceof java.net.SocketException) return true; 
            else if (e instanceof java.net.SocketTimeoutException) return true; 
            else if (e instanceof java.net.UnknownHostException) return true; 
            else if (e instanceof java.net.MalformedURLException) return true; 
            else if (e instanceof java.net.ProtocolException) return true; 
            else if (e instanceof java.net.UnknownServiceException) return true; 
            else return false; 
        }
        
        public void register() throws Exception {
            if (debug) {
                System.out.println("[" + getClass().getSimpleName() + "_register] " + id);
            } 
            
            String path = cluster + "/async/register";
            Map params = new HashMap();
            params.put("id", id);
            params.put("context", context);
            params.put("connection", root.connection); 
            post(path, params); 
        } 
        
        public void unregister() throws Exception {
            if (debug) { 
                System.out.println("[" + getClass().getSimpleName() + "_unregister] " + id);
            } 
            
            String path = cluster + "/async/unregister";
            Map params = new HashMap();
            params.put("id", id);
            params.put("context", context);
            params.put("connection", root.connection); 
            post(path, params); 
        }    
        
        public void push(Object obj) throws Exception {
            if (debug) {
                System.out.println("[" + getClass().getSimpleName() + "_push] id="+ id +", obj"+ obj);
            }
            String path = cluster + "/async/push";
            Map params = new HashMap();
            params.put("id", id);
            params.put("context", context);
            params.put("connection", root.connection); 
            params.put("data", obj);
            post(path, params); 
        }

        public Object poll() throws Exception {
            if (debug) {
                System.out.println("[" + getClass().getSimpleName() + "_poll] id="+ id);
            }
            String path = cluster + "/async/poll";
            Map params = new HashMap();
            params.put("id", id);
            params.put("context", context);
            params.put("connection", root.connection); 
            return post(path, params); 
        } 
        
        public void trace( StringBuilder buffer ) { 
            String path = cluster + "/async/tracert";
            Map params = new HashMap();
            params.put("context", context);
            params.put("connection", root.connection); 
            
            try { 
                buffer.append("\nConnecting to host... "+ host +" (context="+ context +")"); 
                if ( root.connection != null) {
                    buffer.append(", connection="+ root.connection); 
                }
                
                HttpClient client = new HttpClient(host, true);
                Object o = client.post(path, new Object[]{ params });
                buffer.append("\n   Status: Connected"); 
                if ( o != null ) {
                    buffer.append("\n" + o); 
                }
                
            } catch(Throwable t) {
                CustomWriter cw = new CustomWriter(); 
                t.printStackTrace(new PrintWriter(cw)); 
                
                buffer.append("\n   Status: ERROR"); 
                buffer.append("\n" + cw.getText() ); 
            } 
        } 
        
        private void logPCInfo( StringBuilder buffer ) {
            try {
                InetAddress localhost = InetAddress.getLocalHost();
                buffer.append("\n   IP Addr: " + localhost.getHostAddress()); 

                // Just in case this host has multiple IP addresses....
                InetAddress[] allMyIps = InetAddress.getAllByName( localhost.getCanonicalHostName() );
                if (allMyIps != null && allMyIps.length > 1) {
                    for (int i = 0; i < allMyIps.length; i++) {
                        buffer.append("\n            " + allMyIps[i]); 
                    }
                }
            } catch (Throwable t) { 
                //do nothing 
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
