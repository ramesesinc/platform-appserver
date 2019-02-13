/*
 * FieldToMap.java
 *
 * Created on May 20, 2013, 2:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.sql;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class FieldToMap {
    
    
    public static Map convert( Map source  ) {
        return convert(source, null);
    }
    
    public static Map convert( Map source, String excludeFields  ) {
        Map target = new LinkedHashMap();
        if(source==null)return target;
        Map<String, Map> embeddedFields = new LinkedHashMap();
        for(Object o: source.entrySet() ) {
            Map.Entry me = (Map.Entry)o;
            String key = me.getKey().toString();
            boolean _process = false;
            
            if( key.indexOf("_") > 0 && !key.endsWith("_") && (excludeFields==null || key.matches(excludeFields)) ) {
                _process = true;
            }
            
            if(_process) {
                //int pos = key.indexOf("_");
                String arr[] = key.split("_");
                Map c = embeddedFields;
                for(int i=0; i<(arr.length-1);i++) {
                    String kf = arr[i];
                    Map inner = (Map)c.get(kf);
                    if(inner == null ) {
                        inner = new LinkedHashMap();
                        c.put( kf, inner );
                    }
                    c = inner;
                }
                String lastField = arr[arr.length-1];
                c.put( lastField, me.getValue() );
            } else {
                target.put( me.getKey(), me.getValue() );
            }
        }
        //apply the embedded fields
        for(Object m: embeddedFields.entrySet()) {
            Map.Entry me = (Map.Entry)m;
            target.put( me.getKey(), me.getValue() );
        }
        return target;
    }
    
}
