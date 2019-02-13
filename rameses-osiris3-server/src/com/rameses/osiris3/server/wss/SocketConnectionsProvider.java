/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.server.wss;

import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionProvider;
import java.util.Map;

    
/**
 *
 * @author wflores 
 */
public class SocketConnectionsProvider extends XConnectionProvider {

    public String getProviderName() { 
        return "websocket-connection"; 
    }

    public XConnection createConnection(String name, Map conf) { 
        try {
            return new SocketConnectionsImpl(context, conf, name);
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }            
    }
}
