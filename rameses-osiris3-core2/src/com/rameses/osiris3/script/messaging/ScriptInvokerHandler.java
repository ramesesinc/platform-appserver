/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.script.messaging;

import com.rameses.osiris3.xconnection.MessageHandler;
import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
import com.rameses.util.Base64Cipher;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Toshiba
 */
public class ScriptInvokerHandler implements MessageHandler{
    private Map conf;
    private ScriptResponseHandler handler;
    
    public ScriptInvokerHandler(Map conf, ScriptResponseHandler handler){
        this.conf = conf;
        this.handler = handler;
    }
    
    @Override
    public boolean accept(Object data) {
        return true;
    }

    @Override
    public void onMessage(Object data) {
        if(data instanceof Map) {
            Map req = (Map)data;          
            ServiceProxy svc = null;
            String serviceName = (String) req.get("serviceName");
            if (serviceName == null) {
                ScriptServiceContext ctx = new ScriptServiceContext(conf);
                svc = (ServiceProxy)ctx.create( "RemoteMessagingService" );
                try {
                    svc.invoke( "onMessage", new Object[]{req} );
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            
            int idx = serviceName.indexOf(":");
            if ( idx > 0){
                String prefix = serviceName.substring(0, idx);
                Map appconf = new HashMap();
                appconf.put("app.host",  conf.get(prefix+".app.host"));
                appconf.put("app.cluster",  conf.get(prefix+".app.cluster"));
                appconf.put("app.context",  conf.get(prefix+".app.context"));
                appconf.put("readTimeout",  conf.get("readTimeout"));
                ScriptServiceContext ctx = new ScriptServiceContext(appconf);
                svc = (ServiceProxy)ctx.create( serviceName.substring(idx+1));
            }
            else{
                ScriptServiceContext ctx = new ScriptServiceContext(conf);
                svc = (ServiceProxy)ctx.create( "remote/"+ req.get("serviceName"));
            }
            
            String methodName = (String)req.get("methodName");
            Object[] args = (Object[]) req.get("args");
            
            Object res;
            try {
                res = svc.invoke( methodName, args);
            }
            catch(Exception e) {
                res = e;
            }
            
            String encdata = new Base64Cipher().encode( res ); 
            
            Map hparam = new HashMap(); 
            hparam.put("result", encdata); 
            hparam.put("tokenid", req.get("tokenid"));
            handler.send( hparam); 
        }
    }
    
}
