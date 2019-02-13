/*
 * ManagedScriptExecutor.java
 *
 * Created on January 29, 2013, 11:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.annotations.Async;
import com.rameses.annotations.LogEvent;

import com.rameses.annotations.ProxyMethod;
import com.rameses.annotations.RemoteInterface;
import com.rameses.common.AsyncRequest;

import com.rameses.osiris3.data.DataService;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.xconnection.XAsyncConnection;
import com.rameses.osiris3.xconnection.XConnection;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * @author Elmo
 * This can only be created from the TransactionContext
 */
public class ManagedScriptExecutor {
    
    public static final String ASYNC_ID = "_async_";
    
    private static String GET_STRING_INTERFACE = "stringInterface";
    private static String GET_META_INFO = "metaInfo";
    private static String INIT = "_init";
    
    private ScriptExecutor scriptExecutor;
    
    /** Creates a new instance of ManagedScriptExecutor */
    ManagedScriptExecutor(ScriptExecutor s) {
        this.scriptExecutor = s;
    }
    
    public Object execute( final String method, final Object[] args ) throws Exception {
        return execute(method, args, true );
    }
    
    public Object execute( final String method, final Object[] args, boolean bypassAsync  ) throws Exception {
        try {
            boolean fireInterceptors = true;
            ScriptInfo scriptInfo = scriptExecutor.getScriptInfo();
            TransactionContext txn = TransactionContext.getCurrentContext();
            OsirisServer svr = txn.getServer();
            MainContext ct = txn.getContext();
            ScriptService scriptSvc = ct.getService( ScriptService.class );
            ExecutionInfo e = new ExecutionInfo(scriptInfo.getName(),method, args);
            Method m = scriptInfo.getClassDef().findMethodByName( method );
            Map _env = txn.getEnv();
            
            if( method.equals( GET_STRING_INTERFACE )) {
                return scriptInfo.getStringInterface();
            }
            else if( method.equals( GET_META_INFO )) {
                return scriptInfo.getMetaInfo(ct);
            }
            else if( method.equals( INIT ) ) {
                scriptSvc.removeScript( scriptInfo.getName() );
                return null;
            }
            
            //check if this class is a remote method
            RemoteInterface rs = scriptInfo.getClassDef().findClassAnnotation(RemoteInterface.class);
            if( rs != null) { 
                String conn = rs.connection();
                XConnection xconn = ct.getResource(XConnection.class, conn);
                if (xconn == null) throw new Exception("XConnnection "+conn+" does not exist. Please register in connections");
                
                AsyncRequest ar = new AsyncRequest(scriptInfo.getName(), method, args, _env); 
		ar.setContextName((String) xconn.getConf().get("context")); 
		ar.setConnection(conn);
                return ar;
            } 

            if (m == null) {
                throw new NoSuchMethodException("'"+method+"' method does not exist");
            }
            
            if(!bypassAsync) {                
                Async async = m.getAnnotation(Async.class);
                if( async !=null ) {
                    AsyncRequest ar = new AsyncRequest(scriptInfo.getName(), method, args, txn.getEnv());
                    ar.setVarStatus(async.varStatus()); 
                    if(m.getReturnType() != void.class ) {
                        ar.setConnection( async.connection() );
                        
                        XConnection xconn = ct.getResource(XConnection.class, async.connection());
                        if (xconn == null) throw new Exception("XConnnection "+async.connection()+" does not exist. Please register in connections");
                        
                        String context = (String) xconn.getConf().get("context");
                        ar.setContextName(context == null? ct.getName(): context); 
                    }
                    return ar;
                }
            }
            
            //get the necessary resources
            ProxyMethod pma = m.getAnnotation(ProxyMethod.class);
            boolean isProxyMethod = (pma!=null);
            if (isProxyMethod) e.setTag(pma.tag());
            //this is to support old methods. if proxy method marked as local, do not fire interceptors
            if(isProxyMethod && pma.local()) fireInterceptors = false;
           
            
            //we need to check validation in @Params
            CheckedParameter[] checkParams = scriptInfo.getCheckedParameters( method );
            for(CheckedParameter p: checkParams ) {
                if(p.isRequired() && args[p.getIndex()]==null )
                    throw new Exception( "argument " + p.getIndex() + " for method " + method + " must not be null" );
                String schemaName = p.getSchema();
                if(schemaName!=null && schemaName.trim().length()>0) {
                    DataService dataSvc = ct.getService( DataService.class );
                    //dataSvc.validate( schemaName, args[p.getIndex()] );
                }
            }
            
            //inject the dependencies
            DependencyInjector di = scriptSvc.getDependencyInjector();
            di.injectDependencies( scriptExecutor, e );
            
            //fire interceptors
            Object result = null;
            if(fireInterceptors) {
                InterceptorSet s = scriptSvc.findInterceptors( ct, e.toString() );
                InterceptorChain ic = new InterceptorChain(s);
                result = ic.fireChain( new Callable(){
                    public Object call() throws Exception {
                        return scriptExecutor.invokeMethod( method, args );
                    }
                }, e);
            } else {
                result = scriptExecutor.invokeMethod( method, args );
            }
            
            //If method is evented, we publish it in the esb connector
            LogEvent logEvent = m.getAnnotation(LogEvent.class);
            if(logEvent!=null) {
                String eventConnection = logEvent.value();
                if(eventConnection!=null && eventConnection.trim().length()>0) {
                    XConnection conn = ct.getResource( XConnection.class, eventConnection  );
                        
                }
            }
            
            return result;
        } catch(Exception e) {
            throw e;
        }
    }
    
    public void close() {
        this.scriptExecutor.close();
        this.scriptExecutor = null;
    }
    
    public ScriptInfo getScriptInfo() {
        return this.scriptExecutor.getScriptInfo();
    }
    
}
