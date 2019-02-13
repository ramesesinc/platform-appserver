/*
 * OsirisServerBootstrap.java
 *
 * Created on January 10, 2013, 2:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.common;

import com.rameses.server.BootLoader;

/**
 *
 * @author Elmo
 */
public class OsirisServerBootstrap {
    
    
    public static void main(String[] args) throws Exception {
        BootLoader loader = new BootLoader();
        loader.start();
    }
    
   
    
}
