/*
 * DefaultServiceDependencyHandler.java
 *
 * Created on January 10, 2013, 8:53 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.dependency;

import com.rameses.annotations.NullIntf;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.MainContext;

import com.rameses.osiris3.script.DependencyHandler;
import com.rameses.osiris3.script.ExecutionInfo;
import com.rameses.osiris3.script.ManagedScriptExecutor;
import com.rameses.osiris3.script.AsyncScriptInvocation;
import com.rameses.osiris3.script.ScriptInfo;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.script.ScriptInvocation;
import com.rameses.osiris3.script.ScriptTransactionManager;
import com.rameses.osiris3.script.messaging.ScriptConnection;
import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionFactory;
import com.rameses.util.ExceptionManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ServiceDependencyHandler extends DependencyHandler {
    
    public Class getAnnotation() {
        return com.rameses.annotations.Service.class;
    }
    
    public Object getResource(Annotation c, ExecutionInfo einfo) {
        try { 
            com.rameses.annotations.Service annoSvc = (com.rameses.annotations.Service)c;
            DynamicService ds = new DynamicService( annoSvc );
            if ( annoSvc.dynamic() ) {
                return ds; 
            } else {
                String serviceName = annoSvc.value();
                if(serviceName==null || serviceName.trim().length()==0) {
                    serviceName = einfo.getServiceName();
                }

                String connName = annoSvc.connection();
                return ds.lookup( serviceName, connName ); 
            } 
        } catch(Exception e) {
            System.out.println("error injecting resource caused by "+ ExceptionManager.getOriginal(e).getMessage());
            e.printStackTrace();
            return null;
        } 
    }
    
    private Object getResourceImpl(Annotation c, ExecutionInfo einfo) {
        TransactionContext txn = TransactionContext.getCurrentContext();
        AbstractContext ac = txn.getContext();
        try {
            com.rameses.annotations.Service s  = (com.rameses.annotations.Service)c;
            
            //we should not use the transaction service because transactions are only created once. use direct script execution.
            String serviceName = s.value();
            if(serviceName==null || serviceName.trim().length()==0) {
                serviceName = einfo.getServiceName();
            }
            
            //check connection
            String conn = s.connection();
            boolean async = s.async();
            if( conn==null || conn.trim().length()==0) {
                ScriptTransactionManager smr = txn.getManager(ScriptTransactionManager.class);
                final ManagedScriptExecutor executor = smr.create( serviceName );
                final boolean managed = s.managed();
                ScriptInfo sinfo = executor.getScriptInfo();
                InvocationHandler ih = null;
                if( !async ) {
                    ih = new ScriptInvocation((MainContext)ac, serviceName, txn.getEnv(), executor);
                } else {
                    ih = new AsyncScriptInvocation((MainContext)ac, serviceName, txn.getEnv() );
                }
                
                Class localIntf = s.localInterface();
                if(localIntf!=NullIntf.class) {
                    return Proxy.newProxyInstance(sinfo.getClassLoader(), new Class[]{localIntf }, ih);
                } else { 
                    return Proxy.newProxyInstance( sinfo.getInterfaceClass().getClassLoader(), new Class[]{sinfo.getInterfaceClass()}, ih);
                }    
            } else {
                Class localIntf = s.localInterface();
                XConnection xconn = ac.getResource(XConnection.class, conn);
                if (xconn instanceof XConnectionFactory) {
                    XConnectionFactory factory = (XConnectionFactory) xconn;
                    String category = factory.extractCategory(conn);
                    if (category==null || category.length()==0) {
                        xconn = factory.getConnection(s); 
                    } else {
                        xconn = factory.getConnection(category); 
                    } 
                }
                
                ScriptConnection sc = (ScriptConnection)xconn;
                Map env = new HashMap();
                
                Map txnenv = txn.getEnv();                 
                if ( txnenv != null ) { 
                    env.putAll( txnenv );
                }
                
                Iterator keys = sc.getConf().keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next()+"";
                    if (key.startsWith("env.") && key.length() > 4) {
                        env.put(key.substring(4), sc.getConf().get(key));
                    }
                }
                
                try {
                    Integer rt = new Integer(sc.getConf().get("readTimeout").toString());
                    env.put("@read_timeout", rt.intValue()); 
                } catch(Throwable t){;} 
                
                if(localIntf!=null)
                    return sc.create(serviceName, env, localIntf);
                else
                    return sc.create(serviceName, env);
            }
            
        } catch(Exception e) {
            System.out.println("error injecting resource caused by "+ ExceptionManager.getOriginal(e).getMessage());
            e.printStackTrace();
            return null;
        } finally {
            
        }
    }
    
    
    public static class DynamicService {
        
        private com.rameses.annotations.Service annoSvc;
       
        DynamicService( com.rameses.annotations.Service annoSvc ) {
            this.annoSvc = annoSvc; 
        }
        
        public Object lookup( String serviceName ) {
            return lookup( serviceName, null ); 
        }
        
        public Object lookup( String serviceName, String connName ) { 
            try { 
                if ( connName==null || connName.trim().length()==0 ) {
                    return lookupService( serviceName );  
                } else {
                    return lookupService( serviceName, connName ); 
                } 
            } catch(Exception e) { 
                System.out.println("error injecting resource caused by "+ ExceptionManager.getOriginal(e).getMessage());
                e.printStackTrace(); 
                return null; 
            } 
        } 
        
        Object lookupService( String serviceName ) throws Exception {
            if ( serviceName.indexOf(':') > 1 ) {
                return lookupService( serviceName, "remote-script-mq" ); 
            }
            TransactionContext txn = TransactionContext.getCurrentContext();
            AbstractContext ac = txn.getContext();
            
            ScriptTransactionManager smr = txn.getManager(ScriptTransactionManager.class);
            ManagedScriptExecutor executor = smr.create( serviceName );
            ScriptInfo sinfo = executor.getScriptInfo();
            InvocationHandler ih = null;
            
            boolean async = annoSvc.async();            
            if ( async ) {
                ih = new AsyncScriptInvocation((MainContext)ac, serviceName, txn.getEnv() );
            } else {
                ih = new ScriptInvocation((MainContext)ac, serviceName, txn.getEnv(), executor);                
            }

            Class localIntf = annoSvc.localInterface();
            if ( localIntf != NullIntf.class ) {
                return Proxy.newProxyInstance(sinfo.getClassLoader(), new Class[]{localIntf}, ih);
            } else { 
                return Proxy.newProxyInstance( sinfo.getInterfaceClass().getClassLoader(), new Class[]{sinfo.getInterfaceClass()}, ih);
            } 
        } 
        
        Object lookupService( String serviceName, String connName ) throws Exception {
            Class localIntf = annoSvc.localInterface();            
            TransactionContext txn = TransactionContext.getCurrentContext();
            AbstractContext ac = txn.getContext();
            XConnection xconn = ac.getResource(XConnection.class, connName);
            if (xconn instanceof XConnectionFactory) {
                XConnectionFactory factory = (XConnectionFactory) xconn;
                String category = factory.extractCategory(connName);
                if (category==null || category.length()==0) {
                    xconn = factory.getConnection(annoSvc); 
                } else {
                    xconn = factory.getConnection(category); 
                } 
            }

            ScriptConnection sc = (ScriptConnection)xconn;
            Map env = new HashMap();

            Map txnenv = txn.getEnv();                 
            if ( txnenv != null ) { 
                env.putAll( txnenv );
            }

            Iterator keys = sc.getConf().keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next()+"";
                if (key.startsWith("env.") && key.length() > 4) {
                    env.put(key.substring(4), sc.getConf().get(key));
                }
            }

            try {
                Integer rt = new Integer(sc.getConf().get("readTimeout").toString());
                env.put("@read_timeout", rt.intValue()); 
            } catch(Throwable t){;} 

            if (localIntf != null) { 
                return sc.create(serviceName, env, localIntf); 
            } else { 
                return sc.create(serviceName, env); 
            } 
        }
    }
    
}
