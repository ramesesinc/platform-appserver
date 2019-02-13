/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.xconnection;

import java.lang.annotation.Annotation;

/**
 *
 * @author wflores
 */
public abstract class XConnectionFactory extends XConnection {

    public abstract XConnection getConnection(Annotation anno);
    public abstract XConnection getConnection(String category);
    
    public String extractName(String value) {
        if (value == null || value.trim().length() == 0) { 
            return null; 
        } else {
            return value.split(":")[0]; 
        } 
    }
    
    public String extractCategory(String value) {
        if (value == null || value.trim().length() == 0) { return null; }
        
        int idx = value.indexOf(':'); 
        if (idx < 0) { return null; } 
        
        try {
            return value.substring(idx+1).trim(); 
        } catch(Throwable t) {
            return null; 
        }
    }
}
