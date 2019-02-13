/*
 * AsyncServerLoader.java
 *
 * Created on May 27, 2014, 3:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.server.ServerLoader;
import java.util.Date;
import java.util.Map;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 *
 * @author wflores
 */
public class AsyncServerLoader implements ServerLoader {
    
    private String name;
    private Map conf;
    private int port;
    private String context;
    private Server server;
    
    public AsyncServerLoader(String name) {
        this.name = name;
    }
    
    public Map getInfo() {
        return conf;
    }
    
    public String getName() {
        return name;
    }
    
    public void init(String baseUrl, Map info) throws Exception {
        conf = info;
        
        if(!conf.containsKey("cluster")) {
            conf.put("cluster", "osiris3");
        }
        
        port = 8050;
        try {
            port = Integer.parseInt(  this.conf.get("port")+"" );
        } catch(Exception e) {;}
        
        context = "/async";
        if(conf.containsKey("context")) {
            context = (String) conf.get("context");
            if(!context.startsWith("/"))context = "/"+context;
        }
        
        System.out.println("***************************************************************");
        System.out.println("START ASYNC SERVER @ port:" + port + " " + new Date());
        System.out.println("***************************************************************");
    }
    
    public void start() throws Exception {
        server = new Server(port);
        
        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContext.setContextPath(context);
        servletContext.addServlet( new ServletHolder(new AsyncUnregisterServlet()), "/unregister/*" );         
        servletContext.addServlet( new ServletHolder(new AsyncRegisterServlet()), "/register/*" ); 
        servletContext.addServlet( new ServletHolder(new AsyncSendDataServlet()), "/send/*" ); 
        servletContext.addServlet( new ServletHolder(new AsyncGetDataServlet()), "/poll/*" ); 
        
        HandlerList list = new HandlerList();
        list.addHandler( servletContext );
        server.setHandler(list);
        server.start();
        server.join();
    }
    
    public void stop() throws Exception {
        System.out.println("STOPPING ASYNC SERVER @ port:" + port + " " + new Date());
        server.stop();
    }
}
