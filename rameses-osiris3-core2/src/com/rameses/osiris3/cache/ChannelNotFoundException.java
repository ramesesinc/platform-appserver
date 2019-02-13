/*
 * ChannelNotFoundException.java
 *
 * Created on February 19, 2013, 3:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.cache;

/**
 *
 * @author Elmo
 */
public class ChannelNotFoundException extends RuntimeException  {
    
    private String id;
    
    /** Creates a new instance of ChannelNotFoundException */
    public ChannelNotFoundException(String id) {
        this.id = id;
    }
    
}
