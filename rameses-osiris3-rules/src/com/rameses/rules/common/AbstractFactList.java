/*
 * AbstractFactCollection.java
 *
 * Created on June 10, 2013, 5:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

import com.rameses.common.ExpressionResolver;
import com.rameses.common.PropertyResolver;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 * This handles fact collection operations
 */
public abstract class AbstractFactList extends LinkedList {
    
    public FactGroup[] groupBy( String skeyNames ) {
        return groupBy( skeyNames, null );
    }
    
    public FactGroup[] groupBy( String skeyNames, String filter ) {
        String[] keyNames = skeyNames.split(",");
        Map<String, FactGroup> xgroups = new HashMap();
        for( Object o: this ) {
            FactItem fh = (FactItem)o;
            if( filter!=null) {
                boolean test = ExpressionResolver.getInstance().evalBoolean( filter, fh.getData() );
                if(!test) continue;
            }
            
            StringBuilder builder = new StringBuilder();
            Map keys = new HashMap();
            for(int i=0; i<keyNames.length;i++) {
                Object data = PropertyResolver.getInstance().getProperty(fh.getData(), keyNames[i]);
                keys.put( keyNames[i],data );
                if( i>0) builder.append("_");
                builder.append( ( data==null)?"":data);
            }
            String keyId = builder.toString();
            if( !xgroups.containsKey(keyId)) {
                xgroups.put( keyId, new FactGroup(keyId, keys) );
            }
            FactGroup grp = xgroups.get(keyId);
            grp.add( fh );
        }
        return (FactGroup[])xgroups.values().toArray(new FactGroup[]{});
    }
    
    public double sumDouble( String keyName ) {
        return sum(keyName, Double.class);
    }
    
    public int sumInt(String keyName) {
        return sum(keyName, Integer.class);
    }
    
    public BigDecimal sumDecimal(String keyName) {
        return sum(keyName, BigDecimal.class);
    }
    
    public <T> T sum( String keyName ) {
        return (T) sum(keyName, BigDecimal.class);
    }
    
    public <T> T sum( String keyName, Class<T> clazz ) {
        BigDecimal value = new BigDecimal(0);
        for(Object o: this) {
            FactItem f = (FactItem)o;
            Object newValue = PropertyResolver.getInstance().getProperty(  f.getData(), keyName );
            value = value.add(new BigDecimal(newValue.toString())  );
        }
        if(clazz == Double.class) {
            return (T)(new Double(value.doubleValue()));
        } else if(clazz == Integer.class) {
            return (T)(new Integer(value.intValue()));
        }
        return (T)value;
    }
    
    public FactList findAll( String expr ) {
        FactList list = new FactList();
        for( Object ff: this ) {
            FactItem f = (FactItem)ff;
            boolean test = ExpressionResolver.getInstance().evalBoolean( expr, f.getData());
            if(test) list.add(f);
        }
        return list;
    }
    
    public boolean exists( String expr ) {
        List<FactItem> list = new ArrayList();
        for( Object o: this ) {
            FactItem f = (FactItem)o;
            boolean test = ExpressionResolver.getInstance().evalBoolean( expr, f.getData());
            if(test) return true;
        }
        return false;
    }
    
    public void sort(String keyNames) {
        FactListComparator fc = new FactListComparator(keyNames);
        Collections.sort(this, fc);
    }
    
    public boolean empty() {
        return (this.size()==0);
    }
    
    public FactItem getFirstItem() {
        if(this.empty()) return null;
        return (FactItem) this.get(0);
    }
    
    public FactItem getLastItem() {
        if(this.empty()) return null;
        return (FactItem) this.get(this.size()-1);
    }
    
}
