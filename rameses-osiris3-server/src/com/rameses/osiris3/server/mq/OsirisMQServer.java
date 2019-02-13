/*
 * OsirisMQServer.java
 *
 * Created on January 17, 2013, 9:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.mq;

import com.rameses.osiris3.server.common.AbstractServer;
import java.util.Date;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 *
 * @author Elmo
 */
public class OsirisMQServer extends AbstractServer {
    
    private Server server;
    private SocketConnections connections;
    
    public OsirisMQServer(int port) {
        this.port = port;
    }
    
    public OsirisMQServer() {
        this.port = 8090;
    }
    
    public void start() throws Exception {
        System.out.println("STARTING MQ SERVER @"+ new Date());
        server = new Server(port);
        connections = new SocketConnections();
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/osiris3-mq");
        
        context.addServlet(new ServletHolder(new MQJoinServlet(connections)),"/join");
        context.addServlet(new ServletHolder(new MQPollServlet(connections)),"/poll");
        context.addServlet(new ServletHolder(new MQSendServlet(connections)),"/send");
        server.setHandler(context);
        
        server.start();
        server.join();
    }
    
    public void stop() throws Exception {
        System.out.println("STOPPING MQ SERVER @"+ new Date());
        connections.shutdown();
        server.stop();
        server = null;
    }
    
}
