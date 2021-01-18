package com.rameses.fileupload;

import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionProvider;
import java.util.HashMap;
import java.util.Map;


public class FileUploadConnectionProvider extends XConnectionProvider {

    private final static String PROVIDER_NAME = "fileupload";
    
    private Map<String, FileUploadConnectionPool> connections = new HashMap<String, FileUploadConnectionPool>();
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME; 
    }

    @Override
    public XConnection createConnection(String name, Map conf) { 
        String host = getProperty("host", conf);
        String port = getProperty("port", conf);
        String connectionKey = host + ":" + port;
        System.out.println("Creating aws connection: " + connectionKey);
        FileUploadConnectionPool connection = connections.get(connectionKey);
        if (connection == null) {
            connection = new FileUploadConnectionPool(conf, context, name);
            connections.put(connectionKey, connection);
        }
        return connection;
    }
    
    private String getProperty( String name, Map map ) {
        Object o = (map == null? null: map.get(name)); 
        return ( o == null ? "/": o.toString()); 
    } 

}
