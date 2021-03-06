/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mysql;


import com.rameses.osiris3.sql.SqlDialectFunction;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dell
 * DATE_ADD(  date, 1, datepart )
 * datepart sample: DAY, MONTH, YEAR
 */
public class DATE_ADD implements SqlDialectFunction {
    
    protected List<String> params = new ArrayList(); 

    @Override
    public String getName() {
        return "DATE_ADD";
    }

    @Override
    public void addParam(String s) {
        params.add(s);
    }

    public String toString() { 
        StringBuilder sb = new StringBuilder(); 
        sb.append("DATE_ADD( ");
        if( params.size() != 3 )
            throw new RuntimeException("There must be 3 parametetrs in DATE_ADD. (date, interval, datepart)");
        
        String arg0 = params.get(0); 
        String arg1 = params.get(1); 
        String arg2 = params.get(2); 
        sb.append( arg0 + "," );
        sb.append( " INTERVAL " + arg1 + " " + arg2 );
        
        sb.append(")"); 
        return sb.toString();
    }
}
