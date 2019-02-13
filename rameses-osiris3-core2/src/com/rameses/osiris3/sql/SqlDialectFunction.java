/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.sql;

/**
 *
 * @author dell
 */
public interface SqlDialectFunction {
    
    String getName();
    void addParam(String s);
    String toString() ;
}
