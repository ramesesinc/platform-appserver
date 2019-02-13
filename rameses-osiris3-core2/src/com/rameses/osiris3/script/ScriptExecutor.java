/*
 * ScriptExecutor.java
 *
 * Created on January 8, 2013, 9:53 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.osiris3.core.TransactionContext;
import com.rameses.util.ExceptionManager;
import groovy.lang.GroovyObject;

/**
 *
 * @author Elmo
 *
 * This is a wrapper for the actual groovy object. 
 * It has invokeMethod and setProperty
 */
public class ScriptExecutor {
    
    private final static String RESERVED_ACTIONS = "getInterface";
    
    private ScriptInfo scriptInfo;
    private GroovyObject scriptObject;
    private ScriptExecutorPool pool;
    
    public ScriptExecutor(Object obj, ScriptInfo info, ScriptExecutorPool pool) {
        this.scriptInfo = info;
        this.scriptObject = (GroovyObject)obj;
        this.pool = pool;
    }
    
    /** Creates a new instance of ScriptExecutor */
    
    public Object invokeMethod(final String method, final Object[] args ) throws Exception {
        try {
            return  scriptObject.invokeMethod( method, args );
        }
        catch(Exception e) {
            TransactionContext ct = TransactionContext.getCurrentContext();
            if (ct != null) {
                String debug = (String)ct.getContext().getProperty("app.debug");
                if ("true".equals(debug)) e.printStackTrace();
            }
            throw e;
        }
    }
    
    public void setProperty(String name, Object data) {
        scriptObject.setProperty( name, data );
    }
    
    public void destroy() {
        this.scriptInfo = null;
        this.scriptObject = null;
        this.pool = null;
    }
    
    //do this to return it to the pool
    public void close() {
        pool.returnToPool( this );
    }
    

    public ScriptInfo getScriptInfo() {
        return scriptInfo;
    }
}
