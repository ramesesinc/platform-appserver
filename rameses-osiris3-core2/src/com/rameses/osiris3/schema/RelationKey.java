/*
 * RelationKey.java
 *
 * Created on April 29, 2013, 4:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

/**
 *
 * @author Elmo
 */
public class RelationKey {
   
    private String field;
    private String target;
    
    public RelationKey() {
    }

    public String getField() {
        return field;
    }

    public void setField(String key) {
        this.field = key;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
    
}
