/*
 * OsirisTransactionServerProvider.java
 *
 * Created on March 27, 2013, 2:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.server.ServerLoader;
import com.rameses.server.ServerLoaderProvider;

/**
 *
 * @author Elmo
 */
public class OsirisTransactionServerProvider implements ServerLoaderProvider {
    
   
    public String getName() {
        return "osiris3";
    }

    public ServerLoader createServer(String name) {
        return new OsirisTransactionServer(name);
    }
    
}
