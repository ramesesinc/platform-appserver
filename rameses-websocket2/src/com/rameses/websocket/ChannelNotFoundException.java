/*
 * ChannelNotFoundException.java
 *
 * Created on March 2, 2013, 12:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

/**
 *
 * @author Elmo
 */
public class ChannelNotFoundException extends Exception {
    
    /** Creates a new instance of ChannelNotFoundException */
    public ChannelNotFoundException(String name) {
        super("Channel " + name + " not registered");
    }
    
}
