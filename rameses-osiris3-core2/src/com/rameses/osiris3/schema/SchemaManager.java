/*
 * SchemaProviderFactory.java
 *
 * Created on August 13, 2010, 8:23 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import com.rameses.osiris3.persistence.ValidationResult;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 *
 * This class provides the handling of Schema resources
 * This class merely serves Schema objects, it will not involve caching
 * these resources.
 */
public abstract class SchemaManager {
    
    
    public SchemaManager() {
    }
    
    public abstract SchemaConf getConf();
    private Map<String,Schema> cache = Collections.synchronizedMap(new Hashtable());
    
    public Schema getSchema(String sname) {
        String name = sname;
        if(name.indexOf(":")>0) {
            name = sname.substring(0, sname.indexOf(":"));
        }

        //find the schema and check in cache
        Schema schema = cache.get(name);
        if(schema !=null) return schema;
        
        for(SchemaProvider sp: getConf().getProviders()) {
            schema = sp.getSchema(name);
            if( schema!=null ) {
                cache.put(name, schema);
                return schema;
            }
        }
        throw new RuntimeException("Schema " + name +  " cannot be found from provided resources");
    }
    
    public SchemaElement getElement(String name) {
        Schema schema = getSchema(name);
        return schema.getElement(name);
    }
    
    public void destroy() {
        getConf().destroy();
    }
    
    private static SchemaManager instance;
    
    public static void setInstance(SchemaManager sm) {
        instance = sm;
    }
    
    public static SchemaManager getInstance() {
        if(instance==null) {
            instance = new DefaultSchemaFactory();
        }
        return instance;
    }
    
    
    public static class DefaultSchemaFactory extends SchemaManager {
        
        private SchemaConf conf;
        
        public DefaultSchemaFactory() {
            conf = new SchemaConf(this);
        }
        
        public SchemaConf getConf() {
            return conf;
        }
    }
    
    /*
    public Map createMap(String name) {
        SchemaElement element = getElement(name);
        return createMap( element.getSchema(), element );
    }

    public Map createMap(SchemaElement element) {
        return createMap( element.getSchema(), element );
    }
    
    public Map createMap(Schema schema, SchemaElement element) {
        MapBuilderHandler handler = new MapBuilderHandler();
        SchemaScanner scanner = newScanner();
        if(element!=null)
            scanner.scan(schema,element,handler);
        else
            scanner.scan(schema,handler);
        return handler.getMap();
    }
    
    
    public ValidationResult validate(String schemaName, Object data) {
        String sname = schemaName;
        String elementName = null;
        if(schemaName.indexOf(":")>0) {
            sname = schemaName.substring(0, schemaName.indexOf(":"));
            elementName = schemaName.substring(schemaName.indexOf(":")+1);
        }    
        Schema schema = getSchema(sname);
        if(elementName==null)
            return validate( schema.getRootElement(), data, null, null );
        else 
            return validate(schema.getElement(elementName),data, null, null);
    }
    
    public ValidationResult validate(SchemaElement element, Object data) {
        return validate( element, data, null, null );
    }

    public ValidationResult validate(SchemaElement element, Object data, String includeFields, String excludeFields) {
        SchemaValidationHandler handler = new SchemaValidationHandler(includeFields, excludeFields);
        SchemaScanner scanner = newScanner();
        String elementName = element.getName();
        Schema schema = element.getSchema();
        scanner.scan(element.getSchema(),element,data,handler);
        return handler.getResult();
    }
    */
     
    public SchemaSerializer getSerializer() {
        if( getConf().getSerializer()==null)
            throw new RuntimeException("There is no SchemaSerializer defined in SchemaConf");
        return getConf().getSerializer();    
    } 
     
    public List<SchemaElement> lookup(String schemaName, String attribute, String matchPattern ) {
        return getSchema(schemaName).lookup(attribute,matchPattern);
    }

    public Map<String, Schema> getCache() {
        return cache;
    }
    
    
}
