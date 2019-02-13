/*
 * PhaseFact.java
 *
 * Created on June 13, 2013, 3:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

/**
 *
 * @author Elmo
 */
public class PhaseEntry {
    
    private String name;
    private String category;
    
    
    public PhaseEntry(String name) {
        this.name = name;
    }

    public PhaseEntry(String name, String category) {
        this.name = name;
        this.category = category;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCategory() {
        return category;
    }

   
    
    
}
