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
 */
public class GET_AGE implements SqlDialectFunction {
    
    protected List<String> params = new ArrayList(); 

    @Override
    public String getName() {
        return "GET_AGE";
    }

    @Override
    public void addParam(String s) {
        params.add(s);
    }

    public String toString() { 
        if(params.size() != 1) 
            throw new RuntimeException("GET_AGE error. There must be one parameter passed, the date");
        String p = params.get(0);
        StringBuilder sb = new StringBuilder(); 
        sb.append( "TIMESTAMPDIFF(YEAR,"+ p + ", NOW())" );
        return sb.toString();
    }
}
