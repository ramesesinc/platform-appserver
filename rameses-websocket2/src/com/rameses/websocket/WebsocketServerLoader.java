/*
 * WebsocketServerLoader.java
 *
 * Created on March 30, 2013, 8:04 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.websocket;

import com.rameses.server.ServerLoader;
import com.rameses.server.ServerPID;
import com.rameses.util.Service;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 *
 * @author Elmo
 */
public class WebsocketServerLoader implements ServerLoader 
{
    private String name;
    private Map conf;
    private int port;
    private String context;
    private Server server;
    
    private String[] topicChannels;
    private String[] queueChannels;
    private BasicWebsocketHandler wshandler;
    
    public WebsocketServerLoader(String name) {
        this.name = name;
    }
    
    public Map getInfo() {
        return conf;
    }
    
    public void init(String baseUrl, Map info) throws Exception {
        conf = info;
        
        if(!conf.containsKey("cluster")) {
            conf.put("cluster", "osiris3");
        }
        
        port = 8060;
        try {
            port = Integer.parseInt(  this.conf.get("port")+"" );
        } catch(Exception e) {;}
        
        context = "/wschannel";
        if(conf.containsKey("context")) {
            context = (String) conf.get("context");
            if(!context.startsWith("/"))context = "/"+context;
        }        
        if(conf.containsKey("topic-channels")) {
            topicChannels = conf.get("topic-channels").toString().split(",");
        }
        if(conf.containsKey("queue-channels")) {
            queueChannels = conf.get("queue-channels").toString().split(",");
        }
        
        System.out.println("***************************************************************");
        System.out.println("START WEBSOCKET SERVER @ port:" + port + " " + new Date());
        System.out.println("***************************************************************");
    }
    
    public void start() throws Exception {
        server = new Server(port);
        SocketConnections conn = new SocketConnections();

        //locate if there are pre-defined channels in 
        if(topicChannels!=null) {
            for(String s:topicChannels) {
                if(s.trim().length()>0) 
                    conn.addChannel( new TopicChannel(s.trim()));
            }
        }
        
        if(queueChannels!=null) {
            for(String s:queueChannels) {
                if(s.trim().length()>0) 
                    conn.addChannel( new QueueChannel(s.trim()));
            }
        }
        
        //load the channels
        Iterator<ChannelProvider> iter = Service.providers( ChannelProvider.class, WebsocketServerLoader.class.getClassLoader() );
        while(iter.hasNext()) {
            ChannelProvider ch = iter.next();
            for( Channel c: ch.loadChannels()) {
                conn.addChannel( c );
            }
        }
        
        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContext.setContextPath(context);
        servletContext.addServlet( new ServletHolder( new PostMessageServlet(conn)), "/post" );        
        servletContext.addServlet( new ServletHolder( new SendMessageServlet(conn)), "/send" );
        servletContext.addServlet( new ServletHolder( new AddChannelServlet(conn)),  "/addchannel" );
        servletContext.addServlet( new ServletHolder( new RemoveChannelServlet(conn)), "/removechannel" );
        servletContext.addServlet( new ServletHolder( new ManageSocketConnectionsServlet(conn)), "/manage" );
        servletContext.addServlet( new ServletHolder( new ViewSocketConnectionServlet(conn)), "/connection/*" );
        
        wshandler = new BasicWebsocketHandler(conn, conf); 
        HandlerList list = new HandlerList(); 
        list.addHandler( wshandler ); 
        list.addHandler( servletContext ); 
        server.setHandler(list); 
        server.start(); 
        ServerPID.remove(this.name); 
        System.out.println("Server: " + this.name + " has started.");
        server.join(); 
    } 
    
    public void stop() throws Exception {
        System.out.println("STOPPING WEBSOCKET SERVER @ port:" + port + " " + new Date());
        server.stop();
        
        if (wshandler != null) wshandler.close(); 
    }

    public String getName() {
        return name;
    }    
}
