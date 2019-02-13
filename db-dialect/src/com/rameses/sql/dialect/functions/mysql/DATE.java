/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mysql;

import com.rameses.osiris3.sql.SqlDialectFunction;

/**
 *
 * @author Elmo Nazareno
 */
public class DATE implements SqlDialectFunction {
    
    public String getName() {
        return "DATE";
    }

    public void addParam(String s) {
        //do nothing....
    }

    public String toString() {
        return "DATE_FORMAT(NOW(),'%Y-%m-%d' )";
    }
    
}
