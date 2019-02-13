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
 * DAY_DIFF( startdate, enddate )
 */
public class DAY implements SqlDialectFunction {
    
    protected List<String> params = new ArrayList(); 

    @Override
    public String getName() {
        return "DAY";
    }

    @Override
    public void addParam(String s) {
        params.add(s);
    }

    public String toString() { 
        if(params.size() > 1) 
            throw new RuntimeException("DAY error. There must be only 1 parameter passed - date");
        StringBuilder sb = new StringBuilder(); 
        sb.append("DATEPART( DAY, ");
        sb.append( params.get(0)); 
        sb.append(")"); 
        return sb.toString();
    }
}
