/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import java.rmi.server.UID;

/**
 *
 * @author dell
 */
public class UIDKeyGenerator implements KeyGenerator {

    public String getNewKey(String prefix, int len) {
        if( prefix !=null )
            return prefix + (new UID()).toString();
        else
            return (new UID()).toString();
    }
    
}
