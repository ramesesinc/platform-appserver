/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mssql;

import com.rameses.osiris3.sql.SqlDialectFunction;

/**
 *
 * @author Elmo Nazareno
 * Current Date ex. DATE()
 */
public class DATE implements SqlDialectFunction {
    
    public String getName() {
        return "DATE";
    }

    public void addParam(String s) {
        //do nothing....
    }

    public String toString() {
        return "FORMAT( GETDATE(), 'yyyy-MM-dd') ";
    }
    
    
    
    
}
