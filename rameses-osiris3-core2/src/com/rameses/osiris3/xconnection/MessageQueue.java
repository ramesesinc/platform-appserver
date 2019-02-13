/*
 * AsyncQueue.java
 *
 * Created on May 29, 2014, 2:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;



/**
 *
 * @author Elmo
 */
public interface MessageQueue {
    
    void push(Object obj) throws Exception;
    
    Object poll() throws Exception;        
}
