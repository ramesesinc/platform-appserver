/*
 * TransactionContext.java
 *
 * Created on January 28, 2013, 10:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class TransactionContext {
    
    
    protected static final ThreadLocal<TransactionContext> threadLocal = new ThreadLocal();
    
    public static final void setContext(TransactionContext ctx) {
        threadLocal.set(ctx);
    }
    
    public static final void removeContext() {
        threadLocal.remove();
    }
    
    public static final TransactionContext getCurrentContext() {
        return threadLocal.get();
    }
    
    /****************************************************************************
     * start of code
     ****************************************************************************/
    
    private OsirisServer server;
    private MainContext context;
    private Map env;
    
    
    private Map<Class, TransactionManager> managers = new HashMap();
    
    public OsirisServer getServer() {
        return server;
    }
    
    public MainContext getContext() {
        return context;
    }
    
    public Map getEnv() {
        return env;
    }
    
    /** Creates a new instance of TransactionContext */
    public TransactionContext(OsirisServer server, MainContext c, Map env) {
        this.server = server;
        this.context = c;
        this.env = env;
        
        //add the managers
        for(TransactionManager tm: c.getTransactionManagerProvider().getManagers()) {
            managers.put( tm.getClass(), tm );
        }
        
        TransactionContext.setContext( this );
    }
    
    public <T> T getManager(Class<T> clazz) {
        return (T)managers.get( clazz );
    }
    
    
    public void commit() {
        for(TransactionManager t:managers.values() ) {
            t.commit();
        }
    }
    
    public void rollback() {
        for(TransactionManager t:managers.values() ) {
            t.rollback();
        }
    }
    
    public void close() {
        for(TransactionManager t:managers.values() ) {
            t.close();
        }
        managers.clear();
        this.server = null;
        this.context = null;
        this.env = null;
        TransactionContext.removeContext();
    }
    
    
}

