/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.server;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.ContextResource;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.script.ScriptInfo;
import com.rameses.server.ServerPID;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wflores
 */
public class CacheMonitorScriptServlet extends ServiceInvokerServlet {
    
    public String getMapping() { return "/cache/scripts/*"; }
    
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!ServerPID.isCleared()) {
            Object o = new Exception("Server is initializing please wait.");
            writeResponse( o, res );
            return;
        }
        
        String pathinfo = req.getPathInfo();
        if ( pathinfo.startsWith("/")) {
            pathinfo = pathinfo.substring(1);
        } 
        
        String contextName = pathinfo.split("/")[0];
        AppContext ac = OsirisServer.getInstance().getContext( AppContext.class, contextName );
        ContextResource cr = ac.getContextResource( ScriptInfo.class );

        String homepath = req.getContextPath() +"/cache/scripts/"+ contextName;
        String nameToRemove = req.getParameter("remove"); 
        if ( nameToRemove != null && nameToRemove.trim().length() > 0 ) {
            cr.remove( nameToRemove ); 
            res.sendRedirect( homepath ); 
            return;
        }
        
        String actionName = req.getParameter("action"); 
        if ( actionName != null && actionName.matches("refresh|removeall") ) { 
            if ( actionName.equals("removeall")) {
                cr.removeAll();
            }
            res.sendRedirect( homepath ); 
            return; 
        }
        
        List list = new ArrayList();
        cr.copyValues(list);
        Collections.sort(list, new ComparatorImpl());        

        StringBuilder buff = new StringBuilder();
        buff.append("<html><body>");
        buff.append("<style>");
        buff.append("* { font-family: Tahoma, Sanserif; font-size: 10pt; }");
        buff.append("</style>");  
        buff.append("<a href=\"").append( homepath ).append("?action=refresh\">Refresh</a>");
        buff.append("&nbsp;&nbsp;&nbsp;");
        buff.append("<a href=\"").append( homepath ).append("?action=removeall\">Remove All</a>");
        buff.append("<br/><br/>");        
        buff.append("<table cellpadding=\"2\" cellspacing=\"0\">"); 
        buff.append("<tr>");
        buff.append("<th style=\"text-align:right; padding-right:10px;\">#</th>");
        buff.append("<th style=\"text-align:left;\">Service Name</th>");        
        buff.append("</tr>");
        
        long counter = 1;
        DecimalFormat fmt = new DecimalFormat("#,##0");
        for (Object o :  list) {
            if ( o instanceof ScriptInfo ) {
                ScriptInfo si = (ScriptInfo) o; 
                buff.append("<tr>");
                buff.append("<td style=\"text-align:right; padding-right:10px;\">"+ fmt.format(counter) +".</td>");
                buff.append("<td>");
                buff.append( si.getName() ).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                buff.append("<a href=\"").append( homepath );
                buff.append("?remove=").append( si.getName());
                buff.append("\">Remove</a>");
                buff.append("</td>");
                buff.append("</tr>");
                counter++;
            }
        }
        buff.append("</table></body></html>");
        list.clear();
        
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setDateHeader("Expires", System.currentTimeMillis());
        res.setContentType("text/html");
        try { 
            res.getWriter().println( buff.toString());
        } catch (IOException ex) {
            throw new RuntimeException( ex); 
        }
}

    private class ComparatorImpl implements java.util.Comparator {
        public int compare(Object o1, Object o2) {
            ScriptInfo si0 = (o1 instanceof ScriptInfo ? (ScriptInfo) o1 : null); 
            ScriptInfo si1 = (o2 instanceof ScriptInfo ? (ScriptInfo) o2 : null); 
            String str0 = (si0 == null ? o1.toString() : si0.getName());
            String str1 = (si1 == null ? o2.toString() : si1.getName());
            return str0.compareTo(str1); 
        }
    }
}
