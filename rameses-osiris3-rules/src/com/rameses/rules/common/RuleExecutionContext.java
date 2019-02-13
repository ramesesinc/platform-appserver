/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rules.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 *
 * @author dell
 */
public class RuleExecutionContext {
    
    protected static final ThreadLocal<RuleExecutionContext> threadLocal = new ThreadLocal();
    
    public static final void setContext(RuleExecutionContext ctx) {
        threadLocal.set(ctx);
    }
    
    public static final void removeContext() {
        threadLocal.remove();
    }
    
    public static final RuleExecutionContext getCurrentContext() {
        return threadLocal.get();
    }
    
    
    /**************************************************************************
     * Actual class 
     * ************************************************************************/
    private Map result = new HashMap();
    private List facts;
    private Map env = new HashMap();
    
    public RuleExecutionContext(List facts) {
        this.facts = facts;
        RuleExecutionContext.setContext( this );
    }
    
    public void close() {
        RuleExecutionContext.removeContext();
    }

    public Map getResult() {
        return result;
    }

    public List getFacts() {
        return facts;
    }

    public Map getEnv() {
        return env;
    }
    
}
