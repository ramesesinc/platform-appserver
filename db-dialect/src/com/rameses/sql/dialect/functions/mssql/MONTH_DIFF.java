/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mssql;

import com.rameses.sql.dialect.functions.mssql.*;
import com.rameses.osiris3.sql.SqlDialectFunction;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dell
 * MONTH_DIFF( startdate, enddate )
 */
public class MONTH_DIFF implements SqlDialectFunction {
    
    protected List<String> params = new ArrayList(); 

    @Override
    public String getName() {
        return "MONTH_DIFF";
    }

    @Override
    public void addParam(String s) {
        params.add(s);
    }

    public String toString() { 
        if(params.size() != 2) 
            throw new RuntimeException("MONTH_DIFF error. There must be two parameters passed, startdate, enddate");
        StringBuilder sb = new StringBuilder(); 
        sb.append("DATEDIFF ( m, ");
        sb.append( params.get(0)); 
        sb.append(",");
        sb.append( params.get(1)); 
        sb.append(")"); 
        return sb.toString();
    }
}
