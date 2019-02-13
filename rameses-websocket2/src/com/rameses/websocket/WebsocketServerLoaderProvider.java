/*
 * WebsocketServerLoaderProvider.java
 *
 * Created on March 30, 2013, 8:04 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import com.rameses.server.ServerLoader;
import com.rameses.server.ServerLoaderProvider;

/**
 *
 * @author Elmo
 */
public class WebsocketServerLoaderProvider implements ServerLoaderProvider {
    
    public String getName() {
        return "websocket";
    }

    public ServerLoader createServer(String name) {
        return new WebsocketServerLoader(name);
    }
    
}
