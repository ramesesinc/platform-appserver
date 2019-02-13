/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.schema;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author dell
 * The schema relation contains the element, the join type and the joined keys
 * The prefix here refers to the linked prefix. This is to aid 
 * the searching of relation keys.
 */
public class SchemaRelation implements Serializable {
    
    private SchemaElement linkedElement;
    private SchemaElement parent;
    private ComplexField complexField;
    
    //by default loading will be eager.
    private boolean lazyLoad;
    private String orderBy;
    
    public SchemaRelation(SchemaElement parent, ComplexField cf ) {
        this.parent = parent;
        this.complexField = cf;
        String strLazy = (String)cf.getProperty("lazy");
        if(strLazy!=null) {
            try { lazyLoad = Boolean.parseBoolean(strLazy); } catch(Exception e){;}
        }
        orderBy = (String)cf.getProperty("orderBy");
    }

    void setLinkedElement(SchemaElement elem ) {
        this.linkedElement = elem;
    }
    
    public String getJointype() {
        return complexField.getJoinType();
    }

    public SchemaElement getLinkedElement() {
        return linkedElement;
    }

    public SchemaElement getParent() {
        return parent;
    }

    public String getName() {
        return complexField.getName();
    }
    
    public List<RelationKey> getRelationKeys() {
        return complexField.getRelationKeys();
    }
    
    public boolean isRequired() {
        return complexField.isRequired();
    }
    
    public String getRef() {
        return complexField.getRef();
    }

    public String toString() {
        return (getName() +":"+ parent.getName()+"-"+linkedElement.getName());
    }
    
    public int hashCode() {
        return (getName() +":"+ parent.getName()+"-"+linkedElement.getName()).hashCode();
    }

    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }

    public boolean isLazyLoad() {
        return lazyLoad;
    }

    public String getOrderBy() {
        return orderBy;
    }
 
    public String getIncludeFields() {
        return (String) this.complexField.getProperty("includefields");
    }
    
    
    
    
}
