/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.xconnection;

import com.rameses.http.HttpClient;
import com.rameses.osiris3.core.AbstractContext;
import java.util.Map;

/**
 *
 * @author elmonazareno
 */
public class HttpConnectionProvider extends XConnectionProvider {
    
    /** Creates a new instance of EmailXConnection */
    public HttpConnectionProvider() {
    }

    public String getProviderName() {
       return "http";
    }

    public XConnection createConnection(String name, Map conf) {
        return new HttpConnection(name,context, conf);
    }
    
    public static class HttpConnection extends XConnection {

        private AbstractContext ctx;
        private Map conf;
        private String name;
        
        private String host;
        private String context;
        private boolean debug = false;
    
        public HttpConnection(String name, AbstractContext ctx, Map conf) {
            this.conf = conf;
            this.ctx = ctx;
            this.name = name;
            if(!conf.containsKey("host"))
                throw new RuntimeException("Error loading HttpConnection. host is required in conf");
            if( !conf.containsKey("context"))
                throw new RuntimeException("Error loading HttpConnection. context is required in conf");
            this.host = (String)conf.get("host");
            this.context = (String)conf.get("context");
            if(conf.containsKey("debug")) {
                try {
                    this.debug = "true".equalsIgnoreCase(conf.get("debug").toString());
                } 
                catch(Exception ignore){;} 
            };
        }

        public Map getConf() {
            return conf;
        }
        
        @Override
        public void start() {
            //do nothing
        }

        @Override
        public void stop() {
           //do nothing
        }
        
        public Object post( Map params  ) throws Exception {
            return post( null, params);
        }
        
        public Object post( String action, Map params  ) throws Exception {
            String path = host + "/" +context;
            if( action !=null ) {
                if(action.startsWith("/")) path = path + action;
                else path = path + "." + action;
            }
            HttpClient client = new HttpClient(path, false);
            client.setDebug(debug);
            return client.post(params);
        }
        
        public Object get(Map params) throws Exception {
            return get(null, params);    
        }

        public Object get(String action, Map params) throws Exception {
            HttpClient client = new HttpClient(host, false);
            String path = "/" +context;
            if( action !=null ) {
                if(action.startsWith("/")) path = path + action;
                else path = path + "." + action;
            }
            client.setDebug(debug);
            return client.get(path, params);            
        }
        
        
    }
    
    
}
