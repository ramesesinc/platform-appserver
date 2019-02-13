/*
 * FactItemComparator.java
 *
 * Created on June 14, 2013, 9:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

import com.rameses.common.PropertyResolver;
import java.util.Comparator;

/**
 *
 * @author Elmo
 */
public class FactListComparator implements Comparator {
    private String[] fieldNames;
    
    /** Creates a new instance of FactItemComparator */
    public FactListComparator(String fldNames) {
        fieldNames = fldNames.split(",");
    }
    
    public int compare(Object o1, Object o2) {
        FactItem f1 = (FactItem)o1;
        FactItem f2 = (FactItem)o2;
        for(String keyName: fieldNames) {
            Object v1 = PropertyResolver.getInstance().getProperty(  f1.getData(), keyName );
            Object v2 = PropertyResolver.getInstance().getProperty(  f2.getData(), keyName );
            int i = compareValue(v1,v2);
            if(i!=0) return i; 
        }
        return 0;
    }
    
    public int compareValue(Object v1, Object v2) {
        if(v1==null && v2==null) return 0;
        else if(v1==null && v2!=null) return 1;
        else if(v1!=null && v2 == null) return -1;
        else if( v1 instanceof Number ) {
            Number n1 = (Number)v1;
            Number n2 = (Number)v2;
            if( n1.doubleValue() > n2.doubleValue() ) return 1;
            else if( n1.doubleValue() < n2.doubleValue()) return -1;
            return 0;
        } else {
            String s1 = v1.toString();
            String s2 = v2.toString();
            return s1.compareTo(s2);
        }
    }
}