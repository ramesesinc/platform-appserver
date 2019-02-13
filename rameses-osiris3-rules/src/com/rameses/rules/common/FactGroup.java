/*
 * FactHandlerGroup.java
 *
 * Created on June 10, 2013, 2:52 PM
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
public class FactGroup extends AbstractFactList {

    private String keyId;
    private Map keys = new HashMap();
    
    /** Creates a new instance of FactHandlerGroup */
    public FactGroup(String keyId, Map keys) {
        this.keyId = keyId;
        this.keys = keys;
    }
    
    public Map getKeys() {
        return keys;
    }

    public String getKeyId() {
        return keyId;
    }
    
    public Object getKey(String key) {
        return keys.get(key);
    }
    
}

