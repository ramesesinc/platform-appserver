/*
 * AsyncRegisterServlet.java
 *
 * Created on May 27, 2014, 2:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.osiris3.server.common.AbstractServlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wflores 
 */
public class AsyncSendDataServlet extends AbstractServlet 
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
        String id = (String) data.remove("id");
        if (id == null || id.length() == 0) {
            throw new ServletException("Please specify id");
        }
                
        AsyncQueue.put(id, data);
        Map resmap = new HashMap();
        resmap.put("id", id);
        resmap.put("status", "SUCCESS");
        writeResponse(resmap, resp); 
    } 
}