/*
 * WebsocketConnection.java
 *
 * Created on February 9, 2013, 10:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.wsclient;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.osiris3.xconnection.MessageHandler;
import com.rameses.util.Base64Cipher;
import com.rameses.util.MessageObject;
import java.net.ConnectException;
import java.net.URI;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

/**
 *
 * @author Elmo
 */
public class WebsocketConnection extends MessageConnection implements WebSocket.OnTextMessage, WebSocket.OnBinaryMessage 
{
    private final static ExecutorService TASKS = Executors.newCachedThreadPool(); 
    
    private final static int DEFAULT_MAX_CONNECTION     = 35000; 
    private final static int MAX_BINARY_MESSAGE_SIZE    = 16384;    
    private final static int MAX_IDLE_TIME              = 60000; 
    private final static int RECONNECT_DELAY            = 10000; 

    private String connectionid;
    private String name;
    private Map conf; 
    private AbstractContext context;

    private WebSocketClientFactory factory;     
    private WebSocket.Connection connection; 
    private WebSocketClient wsclient; 
    private String protocol; 
    private String group; 
    private String host; 
    private long maxConnection;

    private boolean debug;
    private boolean enabled;
    private String acctname;
    private String apikey;
    
    public WebsocketConnection(String name, AbstractContext context, Map conf) {
        this.connectionid = "WS"+ new UID();
        this.name = name;
        this.conf = conf;
        this.context = context;
        
        protocol = (String) conf.get("ws.protocol");

        maxConnection = DEFAULT_MAX_CONNECTION; 
        if (conf.containsKey("ws.maxConnection")) { 
            maxConnection = Long.parseLong(conf.get("ws.maxConnection")+""); 
        } 
        
        host = (String) conf.get("ws.host");
        if (!host.startsWith("ws")) {
            host = "ws://"+host;
        }
        
        group = (String) conf.get("ws.group");   
        if (group == null || group.length() == 0) group = protocol;

        acctname = (String) conf.get("acctname"); 
        apikey = (String) conf.get("apikey"); 
        debug = "true".equals( conf.get("debug")+"" );

        if ("false".equals(conf.get("ws.enabled")+"")) { 
            enabled = false; 
        } else {
            enabled = true; 
        }
    } 
    
    public Map getConf() { 
        return conf; 
    } 
    
    public void start() {
        try {
            if (!enabled) return;
            
            Map headers = new HashMap();
            headers.put("connectionid", connectionid);
            headers.put("acctname", acctname);  
            headers.put("apikey", apikey);
            headers.put("group", group);

            factory = new WebSocketClientFactory();
            factory.start();
            wsclient = factory.newWebSocketClient();
            wsclient.setProtocol(protocol + ";" + new Base64Cipher().encode(headers));
            wsclient.setMaxBinaryMessageSize(MAX_BINARY_MESSAGE_SIZE);
            open();
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } 
    } 
    
    private void open() throws Exception {
        String shost = host.replaceFirst("ws://", ""); 
        try {
            wsclient.open( new URI(host), this, maxConnection, TimeUnit.MILLISECONDS );
        } catch( InterruptedException ie) {
            System.out.println("[WebsocketConnection, "+ protocol +", "+ shost +"] "+ ie.getClass().getSimpleName() + ": " + ie.getMessage());
        } catch( Exception e ) { 
            boolean allowStackTrace = debug; 
            if ( e instanceof TimeoutException ) {
                allowStackTrace = false; 
            } else {
                System.out.println("[WebsocketConnection, "+ protocol +", "+ shost +"] "+ e.getClass().getName() + ": " + e.getMessage() );
            }
            
            if ( allowStackTrace ) {
                e.printStackTrace();
            } 
            
            TASKS.submit(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep( RECONNECT_DELAY );
                        open(); 
                    } catch(Throwable ign){;} 
                } 
            }); 
        } 
    } 
    
    public void send(Object data) { 
        if (connection == null) {
            System.out.println("[WebsocketConnection] connection is not set");
            return;
        } 
        
        try {
            System.out.println("[WebsocketConnection] send " + data);
            String[] gnames = group.split(",");
            MessageObject mo = new MessageObject();
            mo.setConnectionId(connectionid);
            mo.setGroupId(gnames[0].trim());
            mo.setData(data); 
            byte[] bytes = mo.encrypt();            
            connection.sendMessage( bytes, 0, bytes.length ); 
        } catch (Throwable ex) {
            ex.printStackTrace();
        }        
    }
    
    public void sendText(String data) {
        send( data );
    }
            
    public void onOpen(WebSocket.Connection connection) {
        this.connection = connection;
        connection.setMaxIdleTime( MAX_IDLE_TIME );
    }
    
    public void onClose(int i, String msg) {
        if(connection != null) {
            try { 
                this.connection.close(); 
            } catch(Throwable t) {
                //do nothing 
            } finally { 
                this.connection = null;
            } 
            
            if (i == 1006) {
                try { 
                    factory.stop();  
                } catch(Throwable e){;}
                
                try { 
                    start(); 
                } catch(Throwable e){ 
                    e.printStackTrace(); 
                }
            } else if (i == 1002) {
                System.out.println("[WebsocketConnection, "+ protocol +"] " + msg);
            }
            //reconnect if max idle time reached 
            else if (i == 1000) { 
                try { 
                    open(); 
                } catch(Throwable e) {
                    e.printStackTrace(); 
                }
            }
        }
    }
    
    public void onMessage(String stringData) {
        super.notifyHandlers( stringData );
    }
    
    public void onMessage(byte[] bytes, int offset, int length) {
        try {
            MessageObject mo = new MessageObject().decrypt(bytes, offset, length); 
            //if the sender and receiver uses the same connection, do not process
            if (connectionid.equals(mo.getConnectionId())) return; 

            String msggroup = mo.getGroupId();
            if (msggroup == null && group == null) {
                super.notifyHandlers( mo.getData() ); 
                
            } else if (msggroup != null) { 
                String[] gnames = (group == null? new String[0]: group.split(",")); 
                for (String gname: gnames) {
                    if (msggroup.equalsIgnoreCase(gname)) { 
                        super.notifyHandlers(mo.getData()); 
                        break; 
                    }
                }
            }
        } catch(Exception e) {
            System.out.println("[WebsocketConnection, "+ protocol +"] " + "onMessage failed caused by " + e.getMessage());
            e.printStackTrace();
        } 
    } 
    
    private Object resolveValue(Object value, Map appconf) { 
        if (value == null) return null;
                 
        int startidx = 0; 
        boolean has_expression = false; 
        String str = value.toString();         
        StringBuilder builder = new StringBuilder(); 
        while (true) {
            int idx0 = str.indexOf("${", startidx);
            if (idx0 < 0) break;
            
            int idx1 = str.indexOf("}", idx0); 
            if (idx1 < 0) break;
            
            has_expression = true; 
            String skey = str.substring(idx0+2, idx1); 
            builder.append(str.substring(startidx, idx0)); 
            
            Object objval = appconf.get(skey); 
            if (objval == null) objval = System.getProperty(skey); 
            
            if (objval == null) { 
                builder.append(str.substring(idx0, idx1+1)); 
            } else { 
                builder.append(objval); 
            } 
            startidx = idx1+1; 
        } 
        
        if (has_expression) { 
            builder.append(str.substring(startidx));  
            return builder.toString(); 
        } else { 
            return value; 
        } 
    } 

    @Override
    public void send(Object data, String queueName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addResponseHandler(String tokenid, MessageHandler handler) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
} 
