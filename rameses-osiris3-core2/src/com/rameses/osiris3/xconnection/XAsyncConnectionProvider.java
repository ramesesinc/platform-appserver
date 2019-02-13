/*
 * XAsyncLocalConnectionProvider.java
 *
 * Created on May 29, 2014, 2:39 PM
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
public class XAsyncConnectionProvider extends XConnectionProvider {
    
    public String getProviderName() {
        return "async";
    }

    public XConnection createConnection(String name, Map conf) {
        String host = (String) conf.get("host");
        if(host == null) host = (String) conf.get("app.host");
        
        if(host == null) { 
            return new XAsyncLocalConnection(name, conf); 
        } else { 
            return new XAsyncRemoteConnection(name, conf); 
        } 
    } 

   
}
