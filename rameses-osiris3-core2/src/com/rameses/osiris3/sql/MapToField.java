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
public class MapToField {
    
    private static void collectData(String parentField, String fieldName, Object d, Map target ) {
        if(d instanceof Map) {
            if( parentField == null )
                parentField = fieldName;
            else
                parentField = parentField +"_" + fieldName;
            for(Object o: ((Map)d).entrySet() ) {
                Map.Entry me = (Map.Entry)o;
                collectData( parentField , me.getKey().toString(), me.getValue(), target );
            }
        } else {
            if(parentField!=null)
                fieldName = parentField+"_"+fieldName;
            
            target.put(fieldName, d );
        }
    }
    
    public static Map convert( Map source) {
        return convert(source,null);
    }
    
    public static Map convert( Map source, String excludeFields ) {
        Map target = new LinkedHashMap();
        for(Object o: source.entrySet() ) {
            Map.Entry me = (Map.Entry)o;
            if( excludeFields!=null && me.getKey().toString().matches(excludeFields) ) {
                target.put(me.getKey(), me.getValue());
            } else if( me.getValue() instanceof Map) {
                collectData( null, me.getKey()+"", me.getValue(), target );
            } else {
                target.put(me.getKey(), me.getValue());
            }
        }
        return target;
    }
    
}
