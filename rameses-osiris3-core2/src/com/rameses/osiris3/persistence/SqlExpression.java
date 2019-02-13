/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

/**
 * @author dell
 */
public class SqlExpression {
    private String expression;
    
    public SqlExpression(String s) {
        this.expression = s;
    }
    public String toString() {
        return expression.toString();
    }
}
