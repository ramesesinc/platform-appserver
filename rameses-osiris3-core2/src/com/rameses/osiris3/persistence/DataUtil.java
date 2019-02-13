/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SimpleField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 * Utilities for accessing data in nested format. It also contains other related facilities
 */
public class DataUtil {
    
    
    
    public static String stringifyMapKeys(Map map) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for( Object s: map.keySet() ) {
            if(i++>0) sb.append("|");
            sb.append(s.toString());
        }
        return sb.toString();
    }
    
    
    public static Map buildFinderFromPrimaryKeys( SchemaElement elem, Map data ){
        if(data==null) return null;
        Map mapKey = new HashMap();
        for(SimpleField sf: elem.getPrimaryKeys()) {
            Object val = data.get(sf.getName());
            if( val!=null) {
                mapKey.put( sf.getName(), val );
            }
        }
        if( mapKey.size() == 0 )
            return null;
        return mapKey;
    }
    
    public static void printMap( Map baseData ) {
        for( Object o: baseData.entrySet()) {
            Map.Entry m= (Map.Entry)o;
            System.out.println(m.getKey()+"="+m.getValue());
        }    
    }
    
}
