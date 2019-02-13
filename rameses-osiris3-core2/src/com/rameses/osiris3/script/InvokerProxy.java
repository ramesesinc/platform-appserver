/*
 * InvokerProxy.java
 *
 * Created on February 24, 2013, 11:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.annotations.NullIntf;
import com.rameses.osiris3.core.AppContext;
import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
import com.rameses.service.ServiceProxyInvocationHandler;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public final class InvokerProxy {
    
    private AppContext context;
    private Map conf;
    private Map<String, Class> scripts = Collections.synchronizedMap(  new HashMap() );
    private GroovyClassLoader classLoader;
    private boolean failOnConnectionError;

    public InvokerProxy(AppContext ctx, Map conf) {
        this.conf = conf;
        this.context = ctx;
        this.classLoader = new GroovyClassLoader(ctx.getClassLoader());
        this.failOnConnectionError = true;

        Object val = (conf == null? null: conf.get("failOnConnectionError"));
        if ("false".equals(val+"")) this.failOnConnectionError = false; 
    } 
    
    private interface ScriptInfoInf  {
        String getStringInterface();
    }
    public Object create(String serviceName, Map env) throws Exception{
        return create(serviceName, env, null);
    }
    public Object create(String serviceName, Map env, Class localInterface) throws Exception{
        ScriptServiceContext ect = new ScriptServiceContext(conf);
        //context.get
        if(localInterface!=NullIntf.class && localInterface!=null) {
            return ect.create( serviceName, env, localInterface );
        }
        if( !scripts.containsKey(serviceName) ) {
            try {
                StringBuilder builder = new StringBuilder();
                builder.append( "public class MyMetaClass  { \n" );
                builder.append( "    def invoker; \n");
                builder.append( "    public Object invokeMethod(String string, Object args) { \n");
                builder.append( "        return invoker.invokeMethod(string, args); \n" );
                builder.append( "    } \n");
                builder.append(" } ");
                Class metaClass = classLoader.parseClass( builder.toString() );                    
                scripts.put( serviceName, metaClass  );                                
                /*
                ScriptInfoInf si = ect.create( serviceName,  ScriptInfoInf.class  );
                Class clz = classLoader.parseClass( si.getStringInterface() );
                scripts.put( serviceName, clz );
                 */
            } 
            catch(Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    
        ServiceProxy sp = ect.create( serviceName, env );
        ServiceProxyInvocationHandler si = new ServiceProxyInvocationHandler(sp, failOnConnectionError);
                
        Object obj = scripts.get(serviceName).newInstance();
        ((GroovyObject)obj).setProperty( "invoker", si );
        return obj;        
                
        /*
        Class clz  = scripts.get(serviceName);
        ServiceProxy sp = ect.create( serviceName, env );
        return Proxy.newProxyInstance( classLoader, new Class[]{clz}, new ServiceProxyInvocationHandler(sp) );
         */
    }
    
}
