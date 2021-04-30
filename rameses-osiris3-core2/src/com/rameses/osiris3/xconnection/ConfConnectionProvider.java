/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.xconnection;

import java.util.Map;

/**
 *
 * @author wflores
 */
public class ConfConnectionProvider extends XConnectionProvider {
    
    @Override
    public String getProviderName() {
        return "conf";
    }

    @Override
    public XConnection createConnection(String name, Map conf) {
        return new XConnectionImpl(name, conf); 
    }
    
    private class XConnectionImpl extends XConnection {

        private String name; 
        private Map conf; 
        
        XConnectionImpl( String name, Map conf ) {
            this.name = name; 
            this.conf = conf;
        }
        
        public void start() {
        }

        public void stop() {
        }

        public Map getConf() {
            return conf; 
        }
    }
}
