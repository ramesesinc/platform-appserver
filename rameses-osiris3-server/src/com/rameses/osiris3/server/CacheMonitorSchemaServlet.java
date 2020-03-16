/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.server;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.data.DataService;
import com.rameses.osiris3.schema.Schema;
import com.rameses.osiris3.schema.SchemaManager;
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
public class CacheMonitorSchemaServlet extends ServiceInvokerServlet {
    
    public String getMapping() { return "/cache/schema/*"; }
    
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
        DataService dataSvc = ac.getService( DataService.class );
        SchemaManager sm = dataSvc.getSchemaManager();
        
        String mgrpath = req.getContextPath() +"/cache/manager/"+ contextName;         
        String homepath = req.getContextPath() +"/cache/schema/"+ contextName;
        String nameToRemove = req.getParameter("remove"); 
        if ( nameToRemove != null && nameToRemove.trim().length() > 0 ) {
            sm.remove( nameToRemove );
            res.sendRedirect( homepath ); 
            return; 
        }
        
        String actionName = req.getParameter("action"); 
        if ( actionName != null && actionName.matches("refresh|removeall") ) { 
            if ( actionName.equals("removeall")) {
                sm.removeAll(); 
            }
            res.sendRedirect( homepath ); 
            return; 
        }
        
        List list = new ArrayList();
        sm.copyValues(list);
        Collections.sort(list, new ComparatorImpl());        

        StringBuilder buff = new StringBuilder();
        buff.append("<html><body>");
        buff.append("<style>");
        buff.append("* { font-family: Tahoma, Sanserif; font-size: 10pt; }");
        buff.append("</style>"); 
        buff.append("<a href=\"").append( homepath ).append("?action=refresh\">Refresh</a>");
        buff.append("&nbsp;&nbsp;&nbsp;");
        buff.append("<a href=\"").append( homepath ).append("?action=removeall\">Remove All</a>");
        buff.append("&nbsp;&nbsp;&nbsp;");
        buff.append("<a href=\"").append( mgrpath ).append("\">Manager</a>");
        buff.append("<br/><br/>");
        buff.append("<table cellpadding=\"2\" cellspacing=\"0\">"); 
        buff.append("<tr>");
        buff.append("<th style=\"text-align:right; padding-right:10px;\">#</th>");
        buff.append("<th style=\"text-align:left;\">Schema Name</th>");        
        buff.append("</tr>");
        
        long counter = 1;
        DecimalFormat fmt = new DecimalFormat("#,##0");
        for (Object o :  list) {
            if ( o instanceof Schema ) {
                Schema si = (Schema) o; 
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
            Schema si0 = (o1 instanceof Schema ? (Schema) o1 : null); 
            Schema si1 = (o2 instanceof Schema ? (Schema) o2 : null); 
            String str0 = (si0 == null ? o1.toString() : si0.getName());
            String str1 = (si1 == null ? o2.toString() : si1.getName());
            return str0.compareTo(str1); 
        }
    }
}
