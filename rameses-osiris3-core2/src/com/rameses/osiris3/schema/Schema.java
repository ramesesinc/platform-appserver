/*
 * Schema.java
 *
 * Created on August 12, 2010, 4:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class Schema implements Serializable {
    
    /**
     * this is the manager that created this schema.
     */
    private SchemaManager schemaManager;
    
    //usually this is the first element or the element with the same schema name.
    //or it is the first and the name is null.
    private SchemaElement rootElement;
    private String name;
    
    private Map<String,SchemaElement> elements = new Hashtable();

    private Map<String,SchemaField> schemaFields = new Hashtable();
    
    private Map<String, Relation> relations = new Hashtable();
    
    private Map properties = new HashMap();
    
    /** Creates a new instance of Schema */
    Schema(String n, SchemaManager sm) {
        this.name = n;
        //remove if any extensions
        if( this.name.indexOf(".")>0 ) {
            this.name = this.name.substring(0, this.name.lastIndexOf("."));
        }
        this.schemaManager = sm;
    }
    
    
    //we must ensure that root element must have a name.
    public SchemaElement getRootElement() {
        return rootElement;
    }

    public SchemaElement getElement(String n) {
        //special code. extracts element name from schema:element.
        String elementName = n;
        if(elementName.indexOf(":")>0) {
            elementName = elementName.substring( elementName.indexOf(":")+1 );
        }
        
        if( elementName.equals(name))
            return  rootElement;
        
        SchemaElement e = elements.get(elementName); 
        if(e==null)
            throw new RuntimeException("Schema element " + n + " not found in "+name);
        return e;
    }
    
    /**
     * by default the root element is the first one added.
     */
    public void addElement(SchemaElement se) {
        if(rootElement==null) {
            rootElement = se;
            if(se.getName()==null ) rootElement.setName(name);
        }
        if(se.getName()!=null) {
            elements.put(se.getName(),se);
        }
    }

    public String getName() {
        return name;
    }
    
    /**
     * we follow path : element/fieldname
     * example: order/customer
     */
    private SchemaField _findField( SchemaElement element, String fieldName ) {
        SchemaField retVal = null;
        for(SchemaField sf : element.getFields() ) {
            if(sf instanceof SimpleField) {
                if(sf.getName().equals(fieldName)) {
                    retVal = sf;
                    break;
                }
            }
        }    
        return retVal;
    }
    
    /*
    public SchemaField findField( String path ) {
        if(schemaFields.containsKey(path)) {
            return schemaFields.get(path);
        }

        String elementName = path.substring(0, path.indexOf("/"));
        String fieldName = path.substring(path.indexOf("/")+1 );
        SchemaElement element = getElement(elementName);
        SchemaField sf = _findField( element, fieldName );
        if(sf==null) throw new RuntimeException("schema field " + path + " does not exist!");
        schemaFields.put(path, sf);
        return sf;
    }
    */ 

    public SchemaManager getSchemaManager() {
        return schemaManager;
    }
    
    //this searches for elements that match attributes
    public List<SchemaElement> lookup(String attribute, String matchPattern) {
        List<SchemaElement> list = new ArrayList();
        for(SchemaElement element : elements.values() ) {
            String attrValue = null;
            if( attribute.equals("name")) {
                attrValue = element.getName();
            }
            else {
                attrValue = (String)element.getProperties().get(attribute);
            }
            if(attrValue!=null && attrValue.matches(matchPattern)) {
                list.add(element);
            }
        }
        return list;
    }
    
    
    //new addition to schema. Relations
    public void addRelation(Relation r) {
        if(!relations.containsKey(r.getName())) {
            relations.put( r.getName(), r );
        }
    }
    
    public Relation getRelation(String name) {
        return relations.get(name);
    }
    
    public List<Relation> getAllRelations() {
        return findRelations(null);
    }
    
    public List<Relation> findRelations( String target ) {
        List<Relation> list = new ArrayList();
        Collection<Relation> values = relations.values();
        if(target==null || target.trim().length()==0) {
            list.addAll( values );
        }
        else {
            for(Relation r: values) {
                if(target.equals( r.getTarget() )) list.add( r );
            }
        }
        return list;
    }

    public Map getProperties() {
        return properties;
    }
    
    public String getAdapter() {
        return (String)properties.get("adapter");
    }
}
