/*
 * AsyncUnregisterServlet.java
 *
 * Created on May 27, 2014, 2:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.osiris3.server.common.AbstractServlet;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wflores 
 */
public class AsyncUnregisterServlet extends AbstractServlet 
{    
    public String getMapping() {
        return null;
    }

    public long getBlockingTimeout() {
        return 30000;
    }
    
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {        
        Object[] params = readRequest(req); 
        Map data = (Map)params[0];
        String id = (String) data.get("id");
        if (id == null || id.length() == 0) {
            throw new ServletException("Please specify id");
        }
        
        AsyncQueue.unregister(id); 
        data.put("status", "SUCCESS");
        writeResponse(data, resp); 
    } 
}