/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.common;

import com.rameses.osiris3.script.*;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.TransactionContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 *
 * @author dell
 * This is a utility to lookup services. 
 * This is useful for looking up resources in the rules
 */
public class ServiceUtil {
    
    public static Object lookup(String serviceName) throws Exception {
        TransactionContext txn = TransactionContext.getCurrentContext();
        AbstractContext ac = txn.getContext();
        ScriptTransactionManager smr = txn.getManager(ScriptTransactionManager.class);
        ManagedScriptExecutor executor = smr.create( serviceName );
        ScriptInfo sinfo = executor.getScriptInfo();
        InvocationHandler ih = new ScriptInvocation((MainContext)ac, serviceName, txn.getEnv(), executor);                
        return Proxy.newProxyInstance( sinfo.getInterfaceClass().getClassLoader(), new Class[]{sinfo.getInterfaceClass()}, ih);
    }
    
}
