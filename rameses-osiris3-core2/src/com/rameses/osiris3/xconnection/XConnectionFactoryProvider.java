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
public class XConnectionFactoryProvider extends XConnectionProvider {

    @Override
    public String getProviderName() {
        return "connectionfactory"; 
    }

    @Override
    public XConnection createConnection(String name, Map conf) {
        return new XConnectionFactoryImpl(name, context, conf); 
    } 
    
    public void close(){;}
}
