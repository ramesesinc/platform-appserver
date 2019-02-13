/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mysql;

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
        sb.append("PERIOD_DIFF( ");
        sb.append( "DATE_FORMAT(" );
        sb.append( params.get(1)); 
        sb.append(",'%Y%m')");
        sb.append(",");
        sb.append( "DATE_FORMAT(" );
        sb.append( params.get(0)); 
        sb.append(",'%Y%m')");
        sb.append(")"); 
        return sb.toString();
    }
}
