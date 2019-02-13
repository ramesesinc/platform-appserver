/*
 * AsyncPushServlet.java
 *
 * Created on May 29, 2014, 5:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.server.common.AbstractServlet;
import com.rameses.osiris3.xconnection.MessageQueue;
import com.rameses.osiris3.xconnection.XAsyncConnection;
import com.rameses.osiris3.xconnection.XConnection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Elmo
 */
public class AsyncPushServlet extends AbstractServlet 
{
    private static ExecutorService taskPool;
    
    public void init() throws ServletException {
        taskPool = Executors.newFixedThreadPool(getTaskPoolSize());
    }            
    
    public String getMapping() {
        return "/async/push";
    }

    public long getBlockingTimeout() {
        return 30000;
    }

    public int getTaskPoolSize() {
        return 100; 
    }
    
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {        
        Object[] args = readRequest(req); 
        Object arg0 = (args.length > 0? args[0]: null);        
        List items = new ArrayList();
        if (arg0 instanceof List) {
            items = (List)arg0; 
        } else if (arg0 instanceof Object[]) {
            items = Arrays.asList((Object[]) arg0);
        } else {
            items.add(arg0); 
        }
        
        for (Object obj : items) {
            Map params = (Map)obj;

            String id = (String) params.get("id");
            if (id == null || id.length() == 0) 
                throw new ServletException("Please specify id");
           
            Object odata = params.get("data"); 
            if (odata == null) throw new ServletException("Please specify data");
            
            String context = (String) params.get("context");
            if (context == null) context = "default"; 

            String connection = (String) params.get("connection"); 
            if (connection == null) connection = "async"; 

            AppContext ctx = OsirisServer.getInstance().getContext(AppContext.class, context); 
            XAsyncConnection ac = (XAsyncConnection) ctx.getResource(XConnection.class, connection);

            try { 
                MessageQueue mq = ac.getQueue(id); 
                mq.push(odata); 
            } catch (Exception ex) {
                throw new ServletException(ex.getMessage(), ex); 
            } 
        } 
        
        Map result = new HashMap(); 
        result.put("status", "SUCCESS"); 
        writeResponse(result, resp); 
    } 
} 
