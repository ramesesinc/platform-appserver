/*
 * MQPollServlet.java
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
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

/**
 *
 * @author Elmo
 */
public class MQPollServlet extends HttpServlet {
    
    
    private SocketConnections sockets;
    
    public MQPollServlet(SocketConnections s) {
        this.sockets = s;
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String channelid = request.getParameter("channelid");
        String tokenId = request.getParameter("token");
        
        //check if token exists in the request attribute
        Session session = null;
        Token token = (Token) request.getAttribute("TOKEN");
        if(token==null) {
            session = sockets.getSession(channelid);
            token = session.findToken( tokenId );
            if( token == null ) token = session.add( tokenId );
            request.setAttribute("TOKEN", token);
        } else {
            session = token.getSession();
        }
        
        if(token.hasMessages()) {
            String msg = token.getMessage();
            writeMessage(response,  msg);
        } else {
            Continuation continuation = ContinuationSupport.getContinuation(request);
            if (continuation.isInitial()) {
                // No chat in queue, so suspend and wait for timeout or chat
                continuation.setTimeout(20000);
                continuation.suspend();
                token.setContinuation(continuation);
            } else {
                writeMessage( response, "{status: 'closed'}");
            }
        }
    }
    
    private  void writeMessage(HttpServletResponse response, String msg) throws ServletException, IOException {
        response.setContentType("application/json");
        StringBuilder buf = new StringBuilder();
        buf.append(msg);
        byte[] bytes = buf.toString().getBytes("utf-8");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
    }
    
    
}
