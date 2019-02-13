/*
 * DefaultCacheProvider.java
 *
 * Created on January 16, 2013, 5:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.cache;

import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionProvider;
import java.util.Map;

/**
 *
 * @author Elmo
 * 
 */
public class DefaultCacheProvider extends XConnectionProvider {
    
    public String getProviderName() {
        return "default-cache";
    }
    
    
    //THIS CAN BE USED FOR SINGLE SERVERS ONLY. NOT INTENDED FOR CLUSTERED
    
    //this only holds the keys. not the object

    public XConnection createConnection(String name, Map conf) {
        return new SimpleCache(this, name,conf);
    }


    
    
}
