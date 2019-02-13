/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mysql;

import com.rameses.osiris3.sql.SqlDialectFunction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dell
 * MONTH_ARRAY( int month ) //base 1 example MONTH_ARRAY(1) = 'JAN'
 */
public class MONTH_ARRAY implements SqlDialectFunction{

    protected List<String> params = new ArrayList(); 
    
    public String getName() {
        return "MONTH_ARRAY";
    }

    public void addParam(String s) {
        params.add( s );
    }

    public String toString() {
        String fldname = params.iterator().next();
        StringBuilder sb = new StringBuilder();
        sb.append("CASE " + fldname );
        sb.append( " WHEN 1 THEN 'JAN'");
        sb.append( " WHEN 2 THEN 'FEB'");
        sb.append( " WHEN 3 THEN 'MAR'");
        sb.append( " WHEN 4 THEN 'APR'");
        sb.append( " WHEN 5 THEN 'MAY'");
        sb.append( " WHEN 6 THEN 'JUN'");
        sb.append( " WHEN 7 THEN 'JUL'");
        sb.append( " WHEN 8 THEN 'AUG'");
        sb.append( " WHEN 9 THEN 'SEP'");
        sb.append( " WHEN 10 THEN 'OCT'");
        sb.append( " WHEN 11 THEN 'NOV'");
        sb.append( " WHEN 12 THEN 'DEC'");
        sb.append( " ELSE NULL ");
        sb.append( " END ");
        return sb.toString();
    }
    
    
}
