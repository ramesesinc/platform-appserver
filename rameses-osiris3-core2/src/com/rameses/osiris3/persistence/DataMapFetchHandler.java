/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.SchemaView;
import com.rameses.osiris3.schema.SchemaViewField;
import com.rameses.osiris3.sql.FetchHandler;
import com.rameses.osiris3.sql.FieldToMap;
import com.rameses.util.ObjectDeserializer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
public class DataMapFetchHandler implements FetchHandler {
    
    private SchemaView schemaView;
    
    public DataMapFetchHandler( SchemaView vw ) {
        schemaView = vw;
    }
    
    public List start() {
        return new ArrayList();
    }

    public void end() {
    }
    
    public Object getObject(ResultSet rs) throws Exception {
        Map data = new HashMap();
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        for (int i=0; i<columnCount; i++) {
            String name = meta.getColumnLabel(i+1);
            SchemaViewField fld = schemaView.getField(name);
            Object val = rs.getObject(i+1);
            if(val!=null) {
                if( fld !=null ) {
                    if( fld.isSerialized() ) {
                        val = ObjectDeserializer.getInstance().read(  val.toString() );
                    }
                }
                else if( name.endsWith(".deserialize")) {
                    name = name.substring(0, name.indexOf(".") );
                    val = ObjectDeserializer.getInstance().read(  val.toString() );
                }
            }
            data.put(name, val);
        }
        return FieldToMap.convert(data);
    }

    
}
