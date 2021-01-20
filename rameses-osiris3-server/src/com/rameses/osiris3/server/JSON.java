/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.server;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 *
 * @author ramesesinc
 */
public class JSON {
    
    public String encode( Object data ) {
        if ( data instanceof Map || data instanceof List || data instanceof Object[]) {
            Object json = JSONSerializer.toJSON(data, createConf()); 
            return json.toString(); 
        }
        
        throw new RuntimeException("Cannot encode data to JSON string"); 
    }

    public Object decode( String data ) {
        if ( data != null ) {
            JsonConfig jc = createConf(); 
            net.sf.json.JSON js = JSONSerializer.toJSON(data, jc); 
            if ( js.isArray()) {
                return JSONArray.fromObject( js, jc ); 
            } else {
                return JSONObject.fromObject( js, jc );
            }            
        }
        
        throw new RuntimeException("Cannot decode JSON string to Object"); 
    }
    
    private JsonConfig createConf() {
        JsonConfig jc = new JsonConfig(); 
        jc.registerJsonValueProcessor(java.util.Date.class, new DateValueProcessor()); 
        jc.registerJsonValueProcessor(java.sql.Date.class, new SqlDateValueProcessor()); 
        jc.registerJsonValueProcessor(java.sql.Timestamp.class, new SqlDateTimeValueProcessor()); 
        return jc; 
    }
    

    private class SqlDateValueProcessor implements JsonValueProcessor {

        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
        
        public Object processArrayValue(Object value, JsonConfig jc) { 
            return sdf.format((java.sql.Date) value); 
        }

        public Object processObjectValue(String name, Object value, JsonConfig jc) { 
            return sdf.format((java.sql.Date) value); 
        }
    }    
    
    private class SqlDateTimeValueProcessor implements JsonValueProcessor {

        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        
        public Object processArrayValue(Object value, JsonConfig jc) { 
            return resolve( sdf.format((java.sql.Timestamp) value));
        }

        public Object processObjectValue(String name, Object value, JsonConfig jc) { 
            return resolve( sdf.format((java.sql.Timestamp) value)); 
        }
        
        private String resolve( String value ) {
            String[] arr = value.split(" "); 
            return (arr[0] + "T" + arr[1]); 
        }
    }    

    private class DateValueProcessor implements JsonValueProcessor {

        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        
        public Object processArrayValue(Object value, JsonConfig jc) { 
            return resolve( sdf.format((java.util.Date) value));
        }

        public Object processObjectValue(String name, Object value, JsonConfig jc) { 
            return resolve( sdf.format((java.util.Date) value));
        }
        
        private String resolve( String value ) {
            String[] arr = value.split(" "); 
            return (arr[0] + "T" + arr[1]); 
        }
        
    }   
}
