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
 */
public class JoinLink {
    private String joinType;
    private boolean required;
    private SchemaElement element;
    private String name;
    private List<RelationKey> relationKeys = new ArrayList();
    
    public JoinLink(SchemaElement elem, String name) {
        this.element = elem;
        if(name==null) name = this.element.getName();
        this.name = name;
    }

    public List<RelationKey> getRelationKeys() {
        return relationKeys;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public SchemaElement getElement() {
        return element;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public void setRelationKeys(List<RelationKey> rk) {
        this.relationKeys = rk;
    }
    
}
