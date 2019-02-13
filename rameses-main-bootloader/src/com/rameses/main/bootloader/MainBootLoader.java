/*
 * MainBootLoader.java
 *
 * Created on March 27, 2013, 1:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.main.bootloader;

import com.rameses.server.BootLoader;

/**
 *
 * @author Elmo
 */
public class MainBootLoader {
    
    public static void main(String[] args) throws Exception {
        BootLoader boot = new BootLoader();
        boot.start();
    }
    
}
