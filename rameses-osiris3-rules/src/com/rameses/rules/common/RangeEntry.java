/*
 * RangeEntry.java
 *
 * Created on October 12, 2013, 6:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class RangeEntry {
    
    private String id;
    private double decimalvalue;
    private int intvalue;
    private Map bindings = new HashMap();
    private Map params = new HashMap();
    
    /** Creates a new instance of RangeEntry */
    public RangeEntry(String id) {
        this.id = id;
    }

    public double getDecimalvalue() {
        return decimalvalue;
    }

    public void setDecimalvalue(double decimalvalue) {
        this.decimalvalue = decimalvalue;
    }

    public int getIntvalue() {
        return intvalue;
    }

    public void setIntvalue(int intvalue) {
        this.intvalue = intvalue;
    }

    public Map getBindings() {
        return bindings;
    }

    public void setBindings(Map bindings) {
        this.bindings = bindings;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public String getId() {
        return id;
    }
    
}
