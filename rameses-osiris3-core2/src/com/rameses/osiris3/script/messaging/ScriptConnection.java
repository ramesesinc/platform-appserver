package com.rameses.osiris3.script.messaging;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.script.InvokerProxy;
import com.rameses.osiris3.xconnection.XConnection;
import java.util.Map;


public class ScriptConnection extends XConnection {
    
    private InvokerProxy proxy;
    private Map conf;
    
    public ScriptConnection(String name, AbstractContext ctx, Map conf) {
        this.proxy = new InvokerProxy((AppContext) ctx, conf);
        this.conf = conf;
    }
    
    public void start()  {
        //starting connection
    }
    
    public void stop() {
        //do nothing
    }

    
    public Object create(String serviceName, Map env) throws Exception{
        return  create(serviceName, env, null);
    }
    
    public <T> T create(String serviceName, Map env, Class<T> localInterface) throws Exception{
        return (T) proxy.create( serviceName, env, localInterface );
    }

    public Map getConf() {
        return conf;
    }

}