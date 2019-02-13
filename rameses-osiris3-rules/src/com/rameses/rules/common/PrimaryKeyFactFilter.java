/*
 * PrimaryKeyFactFilter.java
 *
 * Created on June 11, 2013, 10:20 AM
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
public abstract class PrimaryKeyFactFilter implements FactFilter {
    
    private String[] fieldGroup;
    
    public PrimaryKeyFactFilter( String sfields) {
        this.fieldGroup = sfields.split(",");
    }
    
    public String[] getFieldGroup() {
        return fieldGroup;
    }
    
    public abstract int handle( Object oldValue, Object newValue);
    
    public int compare(Object oldValue, Object newValue) {
        //this would be a little slow because we have to check the primary keys
        PropertyResolver resolver = PropertyResolver.getInstance();
        for(String s: fieldGroup) {
            Object k1 = resolver.getProperty( oldValue, s );
            Object k2 = resolver.getProperty( newValue, s );
            if( (k1==null && k2==null) ) continue;
            if( k1 !=null && k1.equals(k2)) continue;
            return FactFilter.RESUME_NEXT;
        }
        return handle(oldValue, newValue );
    }
    
    public static class RetainLastFactFilter extends PrimaryKeyFactFilter {
        public RetainLastFactFilter(String s) {
            super(s);    
        }
        public int handle(Object oldValue, Object newValue) {
            return FactFilter.LOG_SWAP_AND_BREAK;
        }
    };
    
    public static class RetainFirstFactFilter extends PrimaryKeyFactFilter {
        public RetainFirstFactFilter(String s) {
            super(s);    
        }
        public int handle(Object oldValue, Object newValue) {
            return FactFilter.LOG_RETAIN_AND_BREAK;
        }
    };
    
}
