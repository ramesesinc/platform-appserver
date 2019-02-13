/*
 * LogicalFact.java
 *
 * Created on June 13, 2013, 2:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

/**
 *
 * @author Elmo
 * used to flag for system related tasks
 */
public class LogicalFact {
    
    private String name;
    private String tag;
    
    /** Creates a new instance of LogicalFact */
    public LogicalFact(String name, String tag) {
        this.name = name;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }
    
}
