/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.schema;

/**
 *
 * @author dell
 */
public class InverseJoinSchema {
    
    private SchemaRelation relation;
    
    public InverseJoinSchema(SchemaRelation rel) {
        this.relation= rel;
    }

    public SchemaElement getElement() {
        return this.relation.getLinkedElement();
    }

    public SchemaRelation getRelation() {
        return relation;
    }
    
    public String getName() {
        return relation.getName();
    }
    
}
