/*
 * KeyValue.java
 *
 * Created on October 11, 2013, 9:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

import java.io.Serializable;

/**
 *
 * @author Elmo
 */
public class KeyValue implements Serializable {
    
    private String key;
    private String value;
    
    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
    
}
