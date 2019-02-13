/*
 * DataAdapter.java
 *
 * Created on January 30, 2013, 1:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.data;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class DataAdapter extends HashMap {
    
    /** Creates a new instance of DataAdapter */
    public DataAdapter(Map m) {
        super.putAll(m);
    }
    
    public String getDsName() {
        return (String)super.get("dsname");
    }
    
    public String getCatalog() {
        return (String)super.get("catalog");
    }
    
}
