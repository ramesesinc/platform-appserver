/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.schema;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dell
 * This represents both extended or simple linked by many to one or one to one.
 * The SchemaRelation represents the keys.
 * Element is the base element. 
 * parent is a schema view. could be the main schema view or another linked view
 * alias = the table name plus the prefix
 * jointype = extended, one-to-one, many-to-one
 */
public class LinkedSchemaView extends AbstractSchemaView {

    private String jointype;
    private boolean required;
    private String prefix;
    private String includeFields;
    
    private List<SchemaViewRelationField> relationFields = new ArrayList();

    public LinkedSchemaView(String name, SchemaElement element, SchemaView rootVw, AbstractSchemaView parent, String joinType, boolean required, String prefix ) {
        super( ((prefix!=null)?prefix+"_":"")+ name, element);
        super.setParent(parent);
        super.setRootView(rootVw);
        this.jointype = joinType;
        this.required = required;
        this.prefix = prefix;
    }
    
    public void addRelationField(SchemaViewRelationField f) {
        this.relationFields.add(f);
    }
    
    public List<SchemaViewRelationField> getRelationFields() {
        return relationFields;
    }
    
    public boolean isRequired() {
        return required;
    }

    public String getJointype() {
        return jointype;
    }

    public boolean isExtendedView() {
        return getJointype().equals("extended");
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "join_level:" + this.getJoinLevel() + ";" );
        sb.append( "join_" + this.getName()+"->"+this.getParent().getName() + ":" + this.getJointype() + ";");
        int i = 0;
        for( SchemaViewRelationField rf: getRelationFields() ) {
            if( i++>0) sb.append( "+");
            sb.append( rf.getTablealias()+"."+rf.getFieldname() + "=");
            sb.append( rf.getTargetView().getName()+"."+rf.getTargetField().getFieldname());
        }
        return sb.toString();
    }

    public String getPrefix() {
        return prefix;
    }

    public String getIncludeFields() {
        return includeFields;
    }

    public void setIncludeFields(String includeFields) {
        this.includeFields = includeFields;
    }

    
    
}
