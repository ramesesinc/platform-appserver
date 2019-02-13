/*
 * PluginContext.java
 *
 * Created on January 26, 2013, 8:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

/**
 *
 * @author Elmo
 */
public class PluginContext extends MainContext {
    
    public PluginContext( OsirisServer s, String name) {
        super(s);
        super.setName( name );
    }
    
}
