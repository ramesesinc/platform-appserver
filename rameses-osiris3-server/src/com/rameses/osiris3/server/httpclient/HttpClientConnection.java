/*
 * HttpClientConnection.java
 *
 * Created on June 17, 2013, 5:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.osiris3.server.httpclient;

import com.rameses.http.HttpClient;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.xconnection.XConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author wflores
 */
public class HttpClientConnection extends XConnection {

    private final static int QUEUE_SIZE_LIMIT = 20; 
    
    private String name;
    private AbstractContext context;
    private Map conf;
    private LinkedBlockingQueue queue = new LinkedBlockingQueue();
    private ExecutorService executor = Executors.newCachedThreadPool();

    public HttpClientConnection(String name, AbstractContext context, Map conf) {
        this.name = name;
        this.context = context;
        this.conf = (conf == null ? new HashMap() : conf);
    }

    public Map getConf() {
        return conf;
    }

    public void start() {

        executor.submit(new Runnable() {
            public void run() {
                try {
                    execute();
                } catch (Exception ex) {
                    System.out.println("HttpClientConnection.execute [ERROR] " + ex.getMessage());
                }
            }
        });
    }

    public void stop() {
        executor.shutdown();
    }

    public void send(Object message) {
        if (message == null) {
            return;
        }

        if ( queue.size() >= QUEUE_SIZE_LIMIT ) {
            post( message ); 
        } else { 
            queue.add(message); 
        }
    } 
    
    private Object poll( long timeout ) {
        try {
            if ( timeout > 0 ) {
                return queue.poll( timeout, TimeUnit.SECONDS );
            } else {
                return queue.poll(); 
            }
        } catch(Throwable t) {
            return null; 
        }
    }

    private void execute() throws Exception {
        while (true) {
            Object result = poll(1);
            if (result == null) {
                continue;
            }

            ArrayList list = new ArrayList();
            list.add(result);

            int batchSize = 10;
            try {
                batchSize = Integer.parseInt(conf.get("batchSize").toString());
                batchSize = Math.max( batchSize, 0 ); 
            } catch (Throwable t) {;}

            int tries = 1;            
            while ((result = poll(0)) != null) {
                list.add(result);
                tries++;
                if (tries >= batchSize) {
                    break;
                }
            }
            post( list ); 
        }
    }
    
    private void post( Object data ) {
        String host = (String) conf.get("http.host"); 
        String action = (String) conf.get("http.action"); 
        HttpClient httpc = createHttpClient(host); 
        try {
            httpc.post(action, data);
        } catch (Throwable t) {
            System.out.println("[HttpClientConnection.execute]: error in posting data to " + host + " caused by " + t.getMessage()); 
            if ( httpc.isDebug() ) { 
                t.printStackTrace(); 
            } 
        }        
    }

    private HttpClient createHttpClient(String host) {
        HttpClient httpc = new HttpClient(host, true);
        try {
            int value = Integer.parseInt(conf.get("http.connectionTimeout").toString());
            httpc.setConnectionTimeout(value);
        } catch (Throwable t) {;
        }

        try {
            int value = Integer.parseInt(conf.get("http.readTimeout").toString());
            httpc.setReadTimeout(value);
        } catch (Throwable t) {;
        }

        try {
            httpc.setProtocol(conf.get("http.protocol").toString());
        } catch (Throwable t) {;
        }

        try {
            httpc.setDebug("true".equalsIgnoreCase(conf.get("http.debug").toString()));
        } catch (Throwable t) {;
        }

        try {
            boolean bool = "true".equals(conf.get("http.encrypted").toString());
            httpc.setEncrypted(bool);
        } catch (Throwable t) {;
        }

        return httpc;
    }
}
