/*
 * AsyncServerLoderProvider.java
 *
 * Created on May 27, 2014, 3:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.server.ServerLoader;
import com.rameses.server.ServerLoaderProvider;

/**
 *
 * @author wflores
 */
public class AsyncServerLoderProvider implements ServerLoaderProvider {
    
   
    public String getName() {
        return "async";
    }

    public ServerLoader createServer(String name) {
        return new AsyncServerLoader(name);
    }
    
}
