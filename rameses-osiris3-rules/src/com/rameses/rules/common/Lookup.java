/*
 * Fact.java
 *
 * Created on May 29, 2013, 10:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

import java.math.BigDecimal;

/**
 *
 * @author Elmo
 */
public class Lookup {
    
    private String name;
    private String value;
    private int intValue;
    private BigDecimal decimalValue;
    private CallbackHandler handler;
    
    public Lookup(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public BigDecimal getDecimalValue() {
        return decimalValue;
    }

    public void setDecimalValue(BigDecimal decimalValue) {
        this.decimalValue = decimalValue;
    }

    public CallbackHandler getHandler() {
        return handler;
    }

    public void setHandler(CallbackHandler handler) {
        this.handler = handler;
    }

    
}
