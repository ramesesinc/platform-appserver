/*
 * ResourceNotFoundException.java
 *
 * Created on January 26, 2013, 9:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

/**
 *
 * @author Elmo
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Creates a new instance of ResourceNotFoundException
     */
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
    
    public ResourceNotFoundException() {
        
    }

}
