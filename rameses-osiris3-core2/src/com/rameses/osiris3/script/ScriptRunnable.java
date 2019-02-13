/*
 * ScriptRunnable.java
 *
 * Created on February 8, 2013, 8:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.common.AsyncRequest;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.util.ExceptionManager;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ScriptRunnable implements Runnable {
    
    protected MainContext context;
    protected String serviceName;
    protected String methodName;
    protected Object[] args;
    protected Map env;
    private boolean bypassAsync = false;
    
    protected Object result;
    protected Exception err;
    protected IScriptRunnableListener listener;
    private boolean cancelled = false;
    private AsyncRequest asyncRequest;
    
    /**
     * Creates a new instance of ScriptRunnable
     */
    public ScriptRunnable(MainContext ctx) {
        this.context = ctx;
    }
    
    public ScriptRunnable(MainContext context, String serviceName, String methodName, Object[] args, Map env) {
        this.setContext(context);
        this.setServiceName(serviceName);
        this.setMethodName(methodName);
        this.setArgs(args);
        this.setEnv(env);
    }
    
    public void run() {
        //System.out.println("run service method ->" + this.serviceName+"."+this.methodName);
        if(cancelled ) return;
        TransactionContext txn = null;
        try {
            txn = new TransactionContext(context.getServer(), context, env);
            if(listener!=null) listener.onBegin();
            //call the service here.
            //if( getServiceName().contains(":") ) {
            //    RemoteScriptTransactionManager rm = txn.getManager( RemoteScriptTransactionManager.class )
            //    RemoteScriptExecutor rex = rm.create( getServiceName(), context );
            //    result = rex.execute( getMethodName(), getArgs() );
            //}
            ScriptTransactionManager t = txn.getManager( ScriptTransactionManager.class );
            ManagedScriptExecutor mse = t.create( getServiceName());
            result = mse.execute( getMethodName(), getArgs(), isBypassAsync());
            
            
            //if result is instanceof remote service. we are going to wait for a response from the subscribers
            if(result == null) {
                result = "#NULL";
            }
            txn.commit();
            
            if(listener!=null) listener.onComplete( result );
        } catch(Exception ex) {
            txn.rollback(); 
            ex.printStackTrace(); 
            err = ExceptionManager.getOriginal(ex);
            if(listener!=null) listener.onRollback(ex);
        } catch(Throwable t) {
            txn.rollback();
            t.printStackTrace();
            err = new Exception(t.getMessage(), t);
            if(listener!=null) listener.onRollback(err);
        } finally {
            txn.close();
            if(listener!=null) listener.onClose();
        }
    }
    
    public void cancel() {
        cancelled = true;
        if(listener!=null) listener.onCancel();
    }
    
    public Object getResult() {
        return result;
    }
    
    public Exception getErr() {
        return err;
    }
    
    public IScriptRunnableListener getListener() {
        return listener;
    }
    
    public void setListener(IScriptRunnableListener listener) {
        this.listener = listener;
    }
    
    
    
    public MainContext getContext() {
        return context;
    }
    
    public void setContext(MainContext context) {
        this.context = context;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public Object[] getArgs() {
        return args;
    }
    
    public void setArgs(Object[] args) {
        this.args = args;
    }
    
    public Map getEnv() {
        return env;
    }
    
    public void setEnv(Map env) {
        this.env = env;
    }
    
    
    public boolean hasErrs() {
        return err!=null;
    }
    
    public boolean isBypassAsync() {
        return bypassAsync;
    }
    
    public void setBypassAsync(boolean bypassAsync) {
        this.bypassAsync = bypassAsync;
    }
    
    public AsyncRequest getAsyncRequest() {
        return asyncRequest;
    }
    
    public void setAsyncRequest(AsyncRequest asyncRequest) {
        this.asyncRequest = asyncRequest;
    }
}
