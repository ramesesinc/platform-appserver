/*
 * OsirisTransactionServer.java
 *
 * Created on January 10, 2013, 2:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.custom.CustomOsirisServer;
import com.rameses.osiris3.server.common.AbstractServlet;
import com.rameses.server.ServerLoader;
import com.rameses.server.ServerPID;
import com.rameses.util.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


/**
 *
 * @author Elmo
 * This must be matched with rameses-common version 2.0
 * because the client was designed specific for this.
 *
 *  needs: context
 * osiris3/context/DateService.execute
 * osiris3/context/DateService.getClassInfo    - is a reserved word to get class info
 *
 * json/context/DateService.execute
 * jsproxy/context/DateService.js
 *
 * ws/context/DateService?wsdl
 * ws/context/DateService
 *
 * queue-send/channelname/token
 * queue-poll/channelname/token
 *
 *
 */
public class OsirisTransactionServer implements ServerLoader  {
    
    private String name;
    private Server server;
    private Map conf;
    private String home;
    
    private int port = 8090;
    private int taskPoolSize = 100;
    private long blockingTimeout =  20000;
    
    public OsirisTransactionServer(String name) {
        this.name = name; 
    }
    
    public void init(String baseUrl, Map info) throws Exception {

        conf = info;
        
        if(!conf.containsKey("cluster")) {
            conf.put("cluster", "osiris3");
        }
        try {
            port = Integer.parseInt(  this.conf.get("port")+"" );
        } catch(Exception e) {;}

        try {
            blockingTimeout = Long.parseLong(  this.conf.get("blockingTimeout")+"" );
        } catch(Exception e) {;}

        try {
            taskPoolSize = Integer.parseInt(  this.conf.get("taskPoolSize")+"" );
        } catch(Exception e) {;}
        
        home = (String)conf.get( "home.url" );
        if(!home.endsWith("/")) home = home + "/";
        
        System.out.println("***************************************************************");
        System.out.println("START OSIRIS 3 TRANSACTION SERVER @ port:" + port + new Date());
        System.out.println("***************************************************************");
    }
    
    
    
    /******
     * on startup do the ff:
     * - locate the conf file based on osiris.home.
     *      Test first env then system properties
     * - load the osiris.conf
     */
    public void start() throws Exception {
        OsirisServer svr = new CustomOsirisServer(home,conf);
        OsirisServer.setInstance( svr );
        svr.start();
        server = new Server(port);
        
        ArrayList<ServletContextHandler> list = new ArrayList();
        String[] clusters = svr.getCluster().split(",");
        for ( String cname : clusters ) {
            if ( cname.trim().length() == 0 ) continue; 
            
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/"+ cname.trim() );

            //sout this is used by the old system.
            Iterator<AbstractServlet> iter = Service.providers( AbstractServlet.class, getClass().getClassLoader() );
            while(iter.hasNext()) { 
                AbstractServlet ac = iter.next(); 
                ac.setBlockingTimeout( blockingTimeout ); 
                ac.setTaskPoolSize( taskPoolSize ); 
                context.addServlet( new ServletHolder(ac), ac.getMapping() ); 
            } 
            list.add( context ); 
        } 
        
        ContextHandlerCollection handlers = new ContextHandlerCollection(); 
        handlers.setHandlers( list.toArray(new ServletContextHandler[]{})); 
        server.setHandler( handlers ); 
        server.start();
        ServerPID.remove(this.name); 
        System.out.println("Server: "+ this.name +" has started");
        server.join();
    }
    
    public void stop() throws Exception {
        System.out.println("STOPPING OSIRIS 3 TRANSACTION SERVER @ port:" + port + new Date());
        OsirisServer.getInstance().stop();
        server.stop();
    }
    
    public Map getInfo() {
        return conf;
    }
}
