package com.rameses.fileupload;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.osiris3.xconnection.MessageHandler;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUploadConnectionPool extends MessageConnection
{
    private Map conf;
    private Map appConf;
    private AbstractContext context;
    private String name;
    private boolean started;

    private API api;
    private FileUploadConnection connection;

    public FileUploadConnectionPool(Map conf, AbstractContext context, String name){
        this.started = false;
        this.name = name;
        this.conf = conf;
        this.context = context;
        
        appConf = new HashMap();
        appConf.putAll(conf);
        
        api = new API(); 
        api.setHost(getProperty("host")); 
        api.setChannel(getProperty("channel")); 
        api.setDownloadDir(getProperty("downloadDir")); 
        api.setDebug("false".equals(getProperty("debug")+"") ? false : true);
        
        try {
            api.setPort(Integer.parseInt(getProperty("port"))); 
        } catch(Throwable t) {;}
    }
            
    @Override
    public void start() {
        if ( started ) {
            return;
        }
        connection = new FileUploadConnection(name, context, conf);
        connection.setAPI(api);
        connection.start();
        started = true;
        System.out.println("FileUploadConnectionPool [" + api.getChannel() + "] started");
    }

    @Override
    public void stop() {
        if (started) {
            connection.stop();
        }
    }

    @Override
    public Map getConf() {
        return conf;
    }
    
    public Object upload(String key, File file) throws Exception  {
        return upload(key, file, api.getChannel());
    }
    
    public Object upload(String key, File file, String channel) throws Exception  {
        if (started) {
            return connection.upload(key, file, channel);
        }
        return null;
    }
    
    public Object upload(String key, byte[] bytes) throws Exception  {
        return upload(key, bytes, api.getChannel());
    }
    
    public Object upload(String key, byte[] bytes, String channel) throws Exception  {
        if (started) {
            return connection.upload(key, bytes, channel);
        }
        return null;
    }
    
    public Object upload(String key, InputStream is) throws Exception  {
        return upload(key, is, api.getChannel());
    }
    
    public Object upload(String key, InputStream is, String channel) throws Exception  {
        if (started) {
            return connection.upload(key, is, channel);
        }
        return null;
    }
    
    
    public void download(String key) throws Exception  {
        download(key, api.getChannel());
    }
    
    public void download(String key, String channel) throws Exception  {
        if (started) {
            connection.download(key, channel);
        }
    }
    
    public ObjectReader getObject(String key) throws Exception {
        return getObject(key, null);
    }
    
    public ObjectReader getObject(String key, String channel) throws Exception {
        if (started) {
            return connection.getObject(key, channel);
        }
        return null;
    }
    
    public void deleteAll() throws Exception  {
        delete(null, null);
    }
    
    public void delete(String key) throws Exception  {
        delete(key, null);
    }
    
    public void delete(String key, String channel) throws Exception  {
        if (started) {
            connection.delete(key, channel);
        }
    }
    
    public List getList() throws Exception  {
        return getList(null, null);
    }
    
    public List getList(String key) throws Exception  {
        return getList(key, api.getChannel());
    }
    
    public List getList(String key, String channel) throws Exception  {
        if (started) {
            return connection.getList(key, channel);
        }
        return new ArrayList();
    }
    

    @Override
    public void send(Object data) {
    }

    @Override
    public void sendText(String data) {
    }

    @Override
    public void send(Object data, String queueName) {
    }

    @Override
    public void addResponseHandler(String tokenid, MessageHandler handler) throws Exception {
    }
    
    private String getProperty( String name ) {
        return getProperty(name, conf); 
    } 
    private String getProperty( String name, Map map ) {
        Object o = (map == null? null: map.get(name)); 
        return ( o == null ? null: o.toString()); 
    } 
    
}
