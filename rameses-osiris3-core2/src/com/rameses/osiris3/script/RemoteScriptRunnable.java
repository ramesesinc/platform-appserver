/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.script;

import com.rameses.osiris3.cache.CacheConnection;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.osiris3.xconnection.MessageHandler;
import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.util.Base64Cipher;
import com.rameses.util.ExceptionManager;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dell
 * This is mainly used for 
 */
public class RemoteScriptRunnable extends ScriptRunnable implements MessageHandler {
    
    private String hostName;
    private String tokenid;
    
    public RemoteScriptRunnable(MainContext ctx) {
        super(ctx);
    }
    
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }


    @Override
    public boolean accept(Object data) {
        return true;
    }

    /*
    public Object getCacheData( String key ) throws Exception {
        CacheConnection cache = (CacheConnection)context.getResource(XConnection.class, "remote-script-data-cache");
        if(cache==null) {
            throw new Exception("remote-script-data-cache not properly defined in connections" );
        }
        Object obj = cache.get(key);
        //do not store data in cache.
        cache.remove(key);
        return obj;
    }
    */
    
    //listener on close will resume the process in the web
    @Override
    public void run() {
        try {
            //assign the token before hand
            String tokenid = getTokenid();

            if( getMethodName().matches("stringInterface|metaInfo")) {
                CacheConnection cache = (CacheConnection)context.getResource(XConnection.class, "remote-script-cache");
                if(cache==null) {
                    throw new Exception("remote-script-cache not properly defined in connections" );
                }
                Map hostMap = (Map)cache.get("remote-script:"+hostName);
                if(hostMap==null) {
                    throw new Exception("services for " + hostName + " not found " );
                }
                result = hostMap.get(getServiceName());
                if( result == null ) {
                    throw new Exception("service name " + getHostName() + ":" + getServiceName() + " not found!");
                }
                return;
            }
            else {
                String _connName = (String)super.getContext().getConf().get("remote-script-mq");
                if( _connName == null ) _connName = "remote-script-mq";
                System.out.println("Remote script connection is " + _connName );
                MessageConnection xconn = (MessageConnection)context.getResource(XConnection.class, _connName);
                if(xconn==null) {
                    throw new Exception(_connName + " not found or peorperly defined in connections" );
                }
                //attach immediate response handler
                //xconn.addResponseHandler( tokenid, this ); 
                //xconn.start();
                
                //send the header to the destination
                Map map = new HashMap();
                map.put("tokenid", tokenid);
                map.put("exchange", getHostName() );
                
                map.put("serviceName", getServiceName() );
                map.put("name", getServiceName() );
               
                map.put("methodName", getMethodName() );
                map.put("args", getArgs() );
                //send to the host listener
                xconn.send( map, getHostName() );
            }
        }
        catch(Exception ex) {
            err = ex;
        } finally {
            listener.onClose();
        }
    }

    public void onMessage(Object data) {
        try {
            Base64Cipher encoder = new Base64Cipher();
            if (encoder.isEncoded(data.toString())){
                result = encoder.decode(data.toString());
            }
            else{
                result = data;
            }

            if( result instanceof Exception ) {
                throw ExceptionManager.getOriginal((Exception)result);
            }
            MessageConnection xconn = (MessageConnection)context.getResource(XConnection.class, "remote-script-mq");
            if(xconn==null) {
                throw new Exception("remote-script-mq not properly defined in connections" );
            } 
            xconn.removeQueue(getTokenid());
        }
        catch(Exception ex) {
            err = ex;
        }
        finally {
            listener.onClose();
        }
    }

    @Override
    public void cancel() {
        //remove the token
        try {
            MessageConnection xconn = (MessageConnection)context.getResource(XConnection.class, "remote-script-mq");
            if(xconn==null) {
                throw new Exception("remote-script-mq not properly defined in connections" );
            } 
            xconn.removeQueue(getTokenid());
        }
        catch(Exception ign) {
            System.out.println("Error in RemoteScriptRunnable.cancel." + ign.getMessage());
        }
        super.cancel();
    }

    /**
     * @return the tokenid
     */
    public String getTokenid() {
        if (tokenid == null) {
            tokenid = "TOKEN"+new UID();
        }
        return tokenid;
    }

    /**
     * @param tokenid the tokenid to set
     */
    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }
    
    
}
