/*
 * FactHolder.java
 *
 * Created on June 10, 2013, 11:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

import com.rameses.common.PropertyResolver;

/**
 *
 * @author Elmo
 */
public class FactItem  {
    
    private String rulenames;
    private String id;
    private Object data;
    private int salience;
    
    /** Creates a new instance of FactHolder */
    public FactItem(String id, Object data, String ruleid) {
        this(id, data, ruleid, 0);
    }
    
    public FactItem(String id, Object data, String ruleid, int salience) {
        this.data = data;
        this.id = id;
        this.rulenames = ruleid;
        this.salience = salience;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }
    
    public Object getData() {
        return data;
    }
    
    public String toString(){
        return this.id.toString();
    }

    public String getRulenames() {
        return rulenames;
    }

    public void setRulenames(String rulenames) {
        this.rulenames = rulenames;
    }

    public int getSalience() {
        return salience;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getDataValue(String name) {
        return PropertyResolver.getInstance().getProperty(data,name);
    }
    
}
