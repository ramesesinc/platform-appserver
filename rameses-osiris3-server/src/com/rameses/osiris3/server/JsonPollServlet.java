/*
 * AsyncPollServlet.java
 *
 * Created on January 21, 2013, 6:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.common.AsyncResponse;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;


/**
*
* @author Elmo
* extract the path after poll as follows:
* http://<server>:<port>/<cluster>/json-poll/<context>/<connection>/<channel>/<token>
*/
   
public class JsonPollServlet extends AsyncPollServlet {
    
    public String getMapping() {
        return "/json-poll/*";
    }
    
    protected void writeResponse(Object object, HttpServletResponse res) {
        res.setContentType("application/json");
        try {
            AsyncResponse ar = (AsyncResponse)object;
            Map map = new HashMap();
            map.put("status", ar.getStatus());
            map.put("value", ar.getNextValue());
            res.getWriter().println( JsonUtil.toString(map) );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
