/*
 * HttpClientConnectionProvider.java
 *
 * Created on June 17, 2013, 5:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.httpclient;

import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionProvider;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class HttpClientConnectionProvider extends XConnectionProvider 
{
    
    public String getProviderName() { return "http"; }
    
    public XConnection createConnection(String name, Map data) 
    {
        try {
            return new HttpClientConnection(name, context, data);
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    
}