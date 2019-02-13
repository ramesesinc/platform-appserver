/*
 * CheckedParameter.java
 *
 * Created on February 4, 2013, 9:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

/**
 *
 * @author Elmo
 */
public class CheckedParameter {

    
    //validation schema or rule    
    private String schema;
    
    //position of the argument
    private int index;
    
    //requires that object must not be empty or null.
    private boolean required;
    
    //requires that object must not be empty or null.
    private String dataTypes;
    
    public CheckedParameter(String schema, int index, boolean required, String dt) {
        this.schema = schema;
        this.index = index;
        this.required = required;
        if(dt!=null && dt.trim().length()>0) {
            this.dataTypes = dt;
        }
    }

    public String getSchema() {
        return schema;
    }

    public int getIndex() {
        return index;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDataTypes() {
        return dataTypes;
    }
    

    
}
