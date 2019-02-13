/*
 * MessageHandler.java
 *
 * Created on February 5, 2013, 9:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

/**
 *
 * @author Elmo
 */
public interface MessageHandler {
    
    boolean accept(Object data);
    void onMessage(Object data);
    
}
