/*
 * ScriptTransactionManager.java
 *
 * Created on January 30, 2013, 4:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.TransactionManager;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ScriptTransactionManager implements TransactionManager {
    
    private MainContext context;
    
    private Map<String,ManagedScriptExecutor> scripts = new HashMap();
    
    /**
     * Creates a new instance of ScriptTransactionManager
     */
    public ScriptTransactionManager(MainContext c) {
        this.context = c;
    }
    
    public void close() {
        for(ManagedScriptExecutor se: scripts.values()) {
            se.close();
        }
        scripts.clear();
    }
    
    public ManagedScriptExecutor create(String name ) throws Exception {
        if(! scripts.containsKey(name)  ) {
            ScriptService svc = context.getService( ScriptService.class );
            ScriptExecutor se = svc.create( name );
            scripts.put( name, new ManagedScriptExecutor(se) );
        }
        return scripts.get(name);
    }
    
    public final <T> T create( String name, Class<T> classIntf) throws Exception {
        final ManagedScriptExecutor me = create(name);
        return (T) Proxy.newProxyInstance( context.getClassLoader(), new Class[]{ classIntf },
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return me.execute( method.getName(), args );
                    }
                }        
        );
    }
    

    public void commit() {
        //do nothing
    }

    public void rollback() {
        //do nothing
    }
    
    public void submitAsync(Runnable runnable) { 
        context.submitAsync( runnable ); 
    } 
}
