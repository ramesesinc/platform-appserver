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
public class FileConnectionProvider extends XConnectionProvider {

    public String getProviderName() {
        return "file"; 
    }

    public XConnection createConnection(String name, Map conf) {
        return new FileConnection(context, name, conf); 
    } 
}
