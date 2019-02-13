/*
 * SchemaField.java
 *
 * Created on August 12, 2010, 10:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import java.util.Map;

/**
 *
 * @author elmo
 */
public class SimpleField extends SchemaField implements SimpleFieldTypes {
    
    private String name;
    private boolean required;
    private String type;
   
    
    private Class dataTypeClass;
    
    /**
     * if this is provided, this will override the bean mapping.
     */
    private String mapfield;

    public SimpleField() {
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String dataType) {
        this.type = dataType;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    
    private String _description;
    public String toString() {
        if(_description==null) {
            _description = (name==null ? "_unnamed" : name)  
            + (type!=null ? "[" + type + "]":" ")
            + ( required ? " required " : "" );
        }
        return _description;
    }

    public String getMapfield() {
        return mapfield;
    }

    public void setMapfield(String mapfield) {
        this.mapfield = mapfield;
    }

    public boolean isPrimary() {
        try {
            Object prim = super.getProperties().get("primary");
            if( prim == null ) return false;
            if(prim instanceof Boolean) return ((Boolean)prim).booleanValue();
            String s = prim + "";
            return Boolean.parseBoolean(s);
        }
        catch(Exception e) {
            return false;
        }
    }
    
    public String getFieldname() {
        String fieldName = (String)super.getProperties().get("fieldname");
        if(fieldName==null) return name;
        return fieldName;
    }
    
    public void setFieldname(String name) {
        super.getProperties().put("fieldname", name);  
    }
    
    public Class getDataTypeClass() {
        if( dataTypeClass==null) {
            dataTypeClass = SchemaUtil.getFieldClass(this, null);
        }
        return dataTypeClass;
    }
    
    //this verifies the data. Put the other values here.
    public void verify( Object val  )  throws Exception {
        if( isPrimary() ) return;   //for primary keys do nothing.
        if( val == null  ) {
            if( isRequired() ) {
                throw new Exception(  " is required.");
            }
        }
        else if( getDataTypeClass() == Object.class) {
            //do nothing...
        }
        else if( !SchemaUtil.checkType(this, val.getClass()) ) {
            throw new Exception( " data type is incorrect.");
        }
        
        //verify all other things
        if( getDataTypeClass() == String.class) {
            String mask = (String)getProperty("mask");
            if(mask!=null){
                if(  !val.toString().matches(mask)) {
                    throw new Exception(  " value does not match mask pattern");
                }       
            }
        }
    }
    
    public Object getDefaultValue() {
        return null;
        //returns the default value specified.
    }
    
    public  Map toMap() {
        Map map = super.toMap();
        try {
            map.put("datatypeClass",getDataTypeClass());
        }
        catch(Exception ign){;}
        return map;
    }
    
    public String getExpr() {
        return (String)super.getProperties().get("expr");
    }
    
    public void setExpr(String name) {
        super.getProperties().put("expr", name);  
    }
    
}
