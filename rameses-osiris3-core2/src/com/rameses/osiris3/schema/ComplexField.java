/*
 * ComplexField.java
 *
 * Created on August 12, 2010, 10:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elmo
 */
public class ComplexField extends SchemaField implements IRelationalField {
    
    private String name;
    private boolean required = true;
    private boolean merge;
    private String type;
    private String ref;
    private int min;
    private int max;
    private String serializer;
    private List<RelationKey> relationKeys = new ArrayList();
    
    public String getType() {
        return type;
    }
    
    public void setType(String t) {
        this.type = t;
    }
    
    public String getName() {
        return name;
    }
    
    //inverse types will never be required
    public boolean isRequired() {
        return required;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public int getMin() {
        return min;
    }
    
    public void setMin(int min) {
        this.min = min;
    }
    
    public int getMax() {
        return max;
    }
    
    public void setMax(int max) {
        this.max = max;
    }
    
    public String getRef() {
        if( this.ref == null ) return null;
        if( this.ref.indexOf(":")<=0) return this.ref + ":" + this.ref;
        return this.ref;
    }
    
    public void setRef(String ref) {
        this.ref = ref;
    }
    
    public String getSerializer() {
        return serializer;
    }
    
    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public List<RelationKey> getRelationKeys() {
        return relationKeys;
    }

    public void addKey(RelationKey rk) {
        relationKeys.add( rk );
    }

    public String getRelation() {
        return (String)super.getProperty("relation");
    }
    
    public String getTarget() {
        return (String)super.getProperty("target");
    }
    
    public String getJoinType() {
        String joinType= (String)super.getProperty("jointype");
        if( joinType == null ) {
            return null;
        }
        return joinType.toLowerCase();
    }
    
    public String getFieldname() {
        String fldname = (String)super.getProperty("fieldname");
        if(fldname==null) fldname = getName();
        return fldname;
    }
    
    public String getInversekey() {
        return (String)super.getProperty("inversekey");
    }
    
    public boolean isMerge() { return merge; }
    public void setMerge( boolean merge ) {
        this.merge = merge; 
    }

    @Override
    public Map toMap() {
        Map m = super.toMap();
        m.put("serializer", serializer);
        m.put("type", type);
        m.put("ref", ref);
        m.put("min", min);
        m.put("max", max);
        return m;
    }

    @Override
    public void verify(Object val) throws Exception {
        if( val == null && isRequired() ) {
            throw new Exception( getCaption() + " is required.");
        }
    }
    
    
    
}
