/*
 * MapResultHandler.java
 *
 * Created on July 21, 2010, 8:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elmo
 */
public class MapFetchHandler implements FetchHandler {
    
    private String excludeFields;
    private boolean fieldToMap = true;
    
    public void setExcludeFields(String excludeFields) {
        this.excludeFields = excludeFields;
    }

    public void setFieldToMap(boolean fieldToMap) {
        this.fieldToMap = fieldToMap;
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
            data.put(name, rs.getObject(i+1)); 
        }
        if( fieldToMap == true ) {
            return FieldToMap.convert(data, excludeFields);
        }
        else {
           return data; 
        }
    }

    

    
    
}
