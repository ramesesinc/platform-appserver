/*
 * SharedContext.java
 *
 * Created on January 26, 2013, 8:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

/**
 *
 * @author Elmo
 */
public class SharedContext extends AbstractContext {
    
    public SharedContext(OsirisServer s, String name) {
        super(s);
        super.setName( name );
    }   

    public void start() {
    }

    public void stop() {
    }

}
