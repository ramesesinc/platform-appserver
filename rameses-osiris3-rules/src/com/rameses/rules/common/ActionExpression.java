/*
 * Expression.java
 *
 * Created on May 29, 2013, 11:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

import com.rameses.common.ExpressionResolver;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ActionExpression {
    
    private Map params = new HashMap();
    private String statement;
    
    /** Creates a new instance of Expression */
    public ActionExpression(String stmt) {
        this.statement = stmt;
    }
    
    public ActionExpression(String stmt, Map params) {
        this.statement = stmt;
        this.params = params;
    }
    
    public ActionExpression() {
    }
    
    public ActionExpression add(String name, Object value) {
        params.put(name, value);
        return this;
    }
    
    public void setParams(Map p) {
        params = p;
    }
    
    public Map getParams() {
        return params;
    }
    
    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }
    
    public BigDecimal getDecimalValue() {
        return ExpressionResolver.getInstance().evalDecimal(statement,params);
    }
    
    public int getIntValue() {
        return ExpressionResolver.getInstance().evalInt(statement,params);
    }
    
    public double getDoubleValue() {
        return ExpressionResolver.getInstance().evalDouble(statement,params);
    }
    
    public String getStringValue() {
        return ExpressionResolver.getInstance().evalString( "${"+statement+"}",params);
    }
    
    public boolean getBooleanValue() {
        return ExpressionResolver.getInstance().evalBoolean(statement,params);
    }

    public Object eval() {
        return ExpressionResolver.getInstance().eval(statement,params);
    }
    
}


