/*
 * DefaultXConnectionProvider.java
 *
 * Created on February 24, 2013, 9:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import java.util.Map;

/**
 *
 * @author Elmo
 */
public class DefaultXConnectionProvider extends XConnectionProvider {
    
    
    public String getProviderName() {
        return "default-messaging";
    }

    public XConnection createConnection(String name, Map conf) {
        return new DefaultXConnection(conf);
    }
    
    public class DefaultXConnection extends MessageConnection {
        
        private Map conf;
        
        public DefaultXConnection(Map conf) {
            this.conf = conf;
        }

        public void send(Object data) {
        }

        public void sendText(String data) {
        }

        public void start() {
        }

        public Map getConf() {
            return conf;
        }

        public void send(Object data, String queueName) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addResponseHandler(String queueName, MessageHandler handler) throws Exception{
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
}
