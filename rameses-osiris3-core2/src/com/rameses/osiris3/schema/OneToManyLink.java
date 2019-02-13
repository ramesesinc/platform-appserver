/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.schema;

/**
 * This is used to store information regarding one to many views. This should 
 * only be registered in the SchemaView becasue it is the root view.
 * @author dell
 */
public class OneToManyLink {
    
    private String name;
    private String extendedname;
    private SchemaRelation relation;
    private SchemaElement parent;
    private String prefix;
    
    public OneToManyLink( String name, String prefix, SchemaElement p, SchemaRelation sr) {
        this.name = name;
        this.prefix = prefix;
        this.extendedname = ((prefix!=null)?prefix+"_":"")+ name;
        this.parent = p;
        this.relation = sr;
    }
    
    public String getName() {
        return name;
    }

    public String getExtendedname() {
        return extendedname;
    }

    public SchemaRelation getRelation() {
        return relation;
    }

    public SchemaElement getParent() {
        return parent;
    }
   
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(extendedname);
        sb.append(":[");
        sb.append(parent.getName());
        sb.append("]");
        return sb.toString();
    }

    public String getPrefix() {
        return prefix;
    }
    
}
