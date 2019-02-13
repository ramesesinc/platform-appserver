/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mssql;

import com.rameses.osiris3.sql.SqlDialectFunction;

/**
 *
 * @author dell
 */
public class NOW implements SqlDialectFunction{

    public String getName() {
        return "NOW";
    }

    public void addParam(String s) {
        //do nothing....
    }

    public String toString() {
        return "GETDATE()";
    }
    
    
}
