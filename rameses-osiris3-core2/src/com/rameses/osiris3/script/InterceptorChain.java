/*
 * InterceptorChain.java
 *
 * Created on January 28, 2013, 9:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.common.ExpressionResolver;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.util.BreakException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * @author Elmo
 */
public class InterceptorChain {
    
    private InterceptorSet interceptorSet;
    
    /** Creates a new instance of InterceptorChain */
    public InterceptorChain(InterceptorSet i) {
        this.interceptorSet = i;
    }
    
    private void fireInterceptorList(List<InterceptorInfo> interceptors,  ExecutionInfo einfo) throws Exception {
        TransactionContext tc = TransactionContext.getCurrentContext();
        ScriptTransactionManager smr = tc.getManager( ScriptTransactionManager.class );
        MainContext context = (MainContext)tc.getContext();
        
        Map map = new HashMap();
        map.put("args", einfo.getArgs());
        map.put("env", tc.getEnv());
        map.put("tag", einfo.getTag());
        if(einfo.getResult()!=null) {
            map.put("result", einfo.getResult());
        }
        
        for( InterceptorInfo info: interceptors ) {
            //check eval
            if(info.getEval()!=null && info.getEval().trim().length()>0) {
                boolean b = ExpressionResolver.getInstance().evalBoolean( info.getEval(), map );
                if( b == false ) continue;
            }
            
            try {
                //System.out.println("InterceptorChain "+ einfo.getServiceName() + "."+ einfo.getMethodName() + " execute..."); 
                ManagedScriptExecutor me = smr.create(info.getServiceName() );
                Object vresult = me.execute( info.getMethodName(), new Object[]{einfo}, false );
            } catch(BreakException be) {
                System.out.println("Interceptor error " + info.getServiceName()+"."+info.getMethodName() );
                be.printStackTrace();
            } catch(Exception e) {
                System.out.println("Interceptor error " + info.getServiceName()+"."+info.getMethodName() );
                throw e;
            }
        }
    }
    
    public Object fireChain(Callable callable, ExecutionInfo einfo) throws Exception {
        TransactionContext tc = TransactionContext.getCurrentContext();
        OsirisServer server  = tc.getServer();
        AbstractContext context = tc.getContext();
        ScriptTransactionManager smr = tc.getManager( ScriptTransactionManager.class );
       
        //System.out.println("InterceptorChain "+ einfo.getServiceName() + "."+ einfo.getMethodName() + " before interceptors..."); 
        fireInterceptorList( interceptorSet.getBeforeInterceptors(), einfo );
        Object result = callable.call();
        einfo.setResult( result );
        //System.out.println("InterceptorChain "+ einfo.getServiceName() + "."+ einfo.getMethodName() + " after interceptors..."); 
        fireInterceptorList( interceptorSet.getAfterInterceptors(), einfo );
        return result;
    }
    
    
    
    
}
