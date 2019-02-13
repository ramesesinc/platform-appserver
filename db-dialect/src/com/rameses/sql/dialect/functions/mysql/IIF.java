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
 * IIF( expr, true value, false value )
 */
public class IIF implements SqlDialectFunction {
    
    protected List<String> params = new ArrayList(); 

    @Override
    public String getName() {
        return "IIF";
    }

    @Override
    public void addParam(String s) {
        params.add(s);
    }

    public String toString() { 
        if ( params.size() != 3 ) {
            throw new RuntimeException("IIF (mysql) must have three parameters expr, truevalue, falsevalue");
        }
        StringBuilder sb = new StringBuilder(); 
        sb.append("IF( ");
        sb.append( params.get(0) ); 
        sb.append(",");
        sb.append( params.get(1)); 
        sb.append(",");
        sb.append( params.get(2)); 
        sb.append(")"); 
        return sb.toString();
    }
}
