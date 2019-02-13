package com.rameses.osiris3.script;

import com.rameses.common.AsyncHandler;
import com.rameses.osiris3.core.MainContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;



public class AsyncScriptInvocation implements InvocationHandler {
    
    private MainContext ctx;
    private Map env;
    private String serviceName;
    
    public AsyncScriptInvocation(MainContext ctx, String serviceName, Map env) {
        this.ctx = ctx;
        this.serviceName = serviceName;
        this.env = env;
    }
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        int sz = args.length;
        AsyncHandler h = null;
        if(sz == 1 ) {
            Object test = args[0];
            if(test instanceof AsyncHandler) {
                h = (AsyncHandler)test;
                args = new Object[]{};
            }
        } else if( sz > 1) {
            Object test = args[sz-1];
            if(test instanceof AsyncHandler) {
                h = (AsyncHandler)test;
                Object[] newArgs = new Object[sz-1];
                for(int i=0;i<newArgs.length;i++) {
                    newArgs[i] = args[i];
                }
                args = newArgs;
            }
        }
        ScriptRunnable script = new ScriptRunnable(ctx);
        if( h!=null ) {
            final AsyncHandler ah = h;
            script.setListener( new ScriptRunnableListener() {
                public void onComplete(Object result) {
                    if(result!=null && result.toString().equalsIgnoreCase("#NULL")) {
                        result = null;
                    }
                    ah.call( result );
                }
            });
        }
        script.setServiceName( serviceName );
        script.setMethodName( method.getName() );
        script.setEnv( env );
        script.setArgs(args);
        ctx.submitAsync(script);
        return null;
    }
    
}