/*
 * DecimalFact.java
 *
 * Created on October 26, 2013, 10:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

/**
 *
 * @author Elmo
 */
public class IntegerFact {
    
    private String name;
    private int value;
    private Object data;
    
    /** Creates a new instance of DecimalFact */
    public IntegerFact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    
}
