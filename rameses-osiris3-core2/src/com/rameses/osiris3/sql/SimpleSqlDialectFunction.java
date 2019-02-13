/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.sql;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dell
 */
public class SimpleSqlDialectFunction implements SqlDialectFunction {

    protected String name;
    protected List<String> params = new ArrayList();
    
    public SimpleSqlDialectFunction(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void addParam(String s) {
        params.add(s);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( name );
        sb.append( "(");
        int i = 0;
        for(String s: params) {
            if(i++>0) sb.append(",");
            sb.append( s );
        }
        sb.append( ")");
        return sb.toString();
    }

            
    
}
