/*
 * MQJoinServlet.java
 *
 * Created on January 17, 2013, 9:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server.mq;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Elmo
 */
public class MQJoinServlet extends HttpServlet {
    
    
    private SocketConnections sockets;
    
    public MQJoinServlet(SocketConnections s) {
        this.sockets = s;
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String channelid = request.getParameter("channelid");
        sockets.addSession( channelid );
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost( request, response );
    }
    
}
