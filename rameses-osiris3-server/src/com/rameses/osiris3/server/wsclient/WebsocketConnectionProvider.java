/*
 * WebsocketConnectionProvider.java
 *
 * Created on February 9, 2013, 10:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.wsclient;

import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionProvider;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class WebsocketConnectionProvider extends XConnectionProvider 
{
    
    public String getProviderName() { return "websocket"; }
    
    public XConnection createConnection(String name, Map data) 
    {
        try {
            return new WebsocketConnection(name, context, data);
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    } 
}
