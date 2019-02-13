/*
 * XAsyncLocalConnection.java
 *
 * Created on May 27, 2014, 2:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import java.util.List;

/**
 *
 * @author Elmo
 */
public interface XAsyncConnection  {
    
    MessageQueue register(String id) throws Exception;
    
    void unregister(String id) throws Exception;
    
    MessageQueue getQueue( String id ) throws Exception;
    
    
    /**
     * 
     * modified-by: wflores 
     */
    void trace( StringBuilder buffer ); 

}
