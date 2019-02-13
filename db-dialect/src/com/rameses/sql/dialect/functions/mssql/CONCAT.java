/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mssql;

import com.rameses.osiris3.sql.SqlDialectFunction;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dell
 */
public class CONCAT implements SqlDialectFunction {
    
    protected List<String> params = new ArrayList(); 

    @Override
    public String getName() {
        return "CONCAT";
    }

    @Override
    public void addParam(String s) {
        params.add(s);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for( String s: params ) {
            if(i++>0) sb.append(" + ");
            sb.append("CONVERT(VARCHAR(MAX), ").append(s).append(")"); 
        }
        if ( sb.length() > 0 ) {
            sb.insert(0, "(");
            sb.append(")"); 
        }
        return sb.toString();
    }
    
    
    
}
