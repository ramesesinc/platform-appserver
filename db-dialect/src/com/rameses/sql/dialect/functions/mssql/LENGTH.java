/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mssql;

import com.rameses.sql.dialect.functions.mysql.*;
import com.rameses.sql.dialect.functions.mssql.*;
import com.rameses.osiris3.sql.SqlDialectFunction;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dell
 */
public class LENGTH implements SqlDialectFunction {
    
    protected List<String> params = new ArrayList(); 

    @Override
    public String getName() {
        return "LENGTH";
    }

    @Override
    public void addParam(String s) {
        params.add(s);
    }

    public String toString() { 
        StringBuilder sb = new StringBuilder(); 
        sb.append("LEN( ");
        for (int i=0; i<params.size(); i++) {
            if ( i > 0 ) sb.append(","); 

            sb.append( params.get(i) ); 
        }
        sb.append(")"); 
        return sb.toString();
    }
}
