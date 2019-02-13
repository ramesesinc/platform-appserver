/*
 * XConnection.java
 *
 * Created on February 24, 2013, 9:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class XConnection {
    
    
    public abstract void start();
    public abstract void stop();
    public abstract Map getConf();
   
    
}
