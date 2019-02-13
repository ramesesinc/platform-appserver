/*
 * ServerResource.java
 *
 * Created on January 30, 2013, 10:27 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

/**
 *
 * @author Elmo
 */
public abstract class ServerResource {
    
    protected OsirisServer server;
    
    public ServerResource(OsirisServer s) {
        this.server = s;
    }
    
    public abstract void destroy();
    
}
