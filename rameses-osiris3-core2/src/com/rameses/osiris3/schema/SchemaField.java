/*
 * SchemaField.java
 *
 * Created on August 12, 2010, 10:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author elmo
 */
public abstract class SchemaField implements Serializable{
    
    private SchemaElement element;
    private Map properties = new HashMap();
    
    public SchemaField() {
    }
    
    public Map getProperties() {
        return properties;
    }

    public abstract String getName();
    public abstract boolean isRequired();
    public abstract String getFieldname();
    public abstract void verify( Object val  )  throws Exception;
    
    public  Map toMap() {
        Map map = new HashMap();
        map.putAll( properties );
        map.put("name", getName());
        map.put("fieldname", getFieldname());
        map.put("required",isRequired());
        return map;
    }
    
    public SchemaElement getElement() {
        return element;
    }

    //can be set only by the parent.
    public void setElement(SchemaElement element) {
        this.element = element;
    }
    
    public Object getProperty(String name) {
        return this.properties.get(name);
    }
    
    public String getCaption() {
        String caption = (String)getProperty("caption");
        if( caption == null ) return getName();
        return caption;
    }
    
}
