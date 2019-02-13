/*
 * ScriptInfoScriptProviderService.java
 *
 * Created on July 30, 2013, 10:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;


import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.TransactionContext;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Proxy;

/**
 *
 * @author Elmo
 */
public class ScriptInfoScriptProviderService extends ScriptInfoContextResource {
    
    private static String SERVICE_NAME = "ScriptProviderService";
    
    public ScriptInfo findResource(String name) {
        InputStream is = null;
        TransactionContext txn = TransactionContext.getCurrentContext();
        AbstractContext ac = txn.getContext();
        try {
            ClassLoader classLoader = ac.getClassLoader();
            ScriptTransactionManager smr = txn.getManager(ScriptTransactionManager.class);
            final ManagedScriptExecutor executor = smr.create( SERVICE_NAME );
            ScriptInvocation si = new ScriptInvocation((MainContext)ac, SERVICE_NAME, txn.getEnv(), executor);
            ScriptProviderService svc = (ScriptProviderService)Proxy.newProxyInstance( classLoader, new Class[]{ ScriptProviderService.class }, si);
            String s = svc.getScript(name);
            is = new ByteArrayInputStream(s.getBytes());
            return parseScript(name, is, null, ac );
        } catch(RuntimeException re) {
            re.printStackTrace();
            throw re;
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {is.close();}catch(Exception ign){;}
        }
    }
    
}
