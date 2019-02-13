/*
 * JsProxy.java
 *
 * Created on February 5, 2013, 5:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.osiris3.server.common.AbstractServlet;

/**
 *
 * @author Elmo
 */
public class JsProxy extends AbstractServlet {
    
    public String getMapping() {
        return "/js-proxy/*";
    }
    
}
