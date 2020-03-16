/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.server;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.ContextResource;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.data.DataService;
import com.rameses.osiris3.script.InterceptorSet;
import com.rameses.osiris3.script.ScriptInfo;
import com.rameses.osiris3.sql.SqlUnitCache;
import com.rameses.server.ServerPID;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author wflores
 */
public class CacheMonitorManagerServlet extends ServiceInvokerServlet {
    
    public String getMapping() { return "/cache/manager/*"; }
    
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
        
        String homepath = req.getContextPath() +"/cache/manager/"+ contextName;
        String action = req.getParameter("action"); 
        if ( action != null ) {
            if ( action.equalsIgnoreCase("clearScript")) {
                ContextResource cr = ac.getContextResource( ScriptInfo.class );
                if ( cr != null ) cr.removeAll(); 
            }
            else if ( action.equalsIgnoreCase("clearInterceptors")) {
                ContextResource cr = ac.getContextResource( InterceptorSet.class );
                if ( cr != null ) cr.removeAll(); 
            }
            else if ( action.equalsIgnoreCase("clearSchema")) {
                DataService dataSvc = ac.getService( DataService.class );
                dataSvc.clearSchema(); 
            }
            else if ( action.equalsIgnoreCase("clearSql")) {
                DataService dataSvc = ac.getService( DataService.class );
                dataSvc.clearSql(); 
                SqlUnitCache.clear();
            }
            res.sendRedirect( homepath ); 
            return; 
        }
        
        StringBuilder buff = new StringBuilder();
        buff.append("<html><body>");
        buff.append("<style>");
        buff.append("* { font-family: Tahoma, Sanserif; font-size: 14pt; }");
        buff.append("h3 { font-size: 18pt; }");
        buff.append(".key { font-size: 16pt; padding-right: 20px; }");
        buff.append(".actions > * { font-size: 12pt; }");
        buff.append("tr.filler > td { padding-top:10px; font-size: 1px; }");
        buff.append("</style>");     
        buff.append("<h3>Cache Manager</h3>");
        buff.append("<table cellpadding=\"2\" cellspacing=\"0\" border=\"0\">"); 
        buff.append("<tr>");
        buff.append("   <td class=\"key\">Services</td>");
        buff.append("   <td class=\"actions\">");
        buff.append("      <a href=\""+ homepath +"?action=clearScript\">Clear Cache</a>&nbsp;");
        buff.append("      <a href=\""+ req.getContextPath() +"/cache/scripts/"+ contextName +"\">View Details</a>");
        buff.append("   </td>");
        buff.append("</tr>");
        buff.append("<tr class=\"filler\"><td>&nbsp;</td></tr>");
        buff.append("<tr>");
        buff.append("   <td class=\"key\">Interceptors</td>");
        buff.append("   <td class=\"actions\">");
        buff.append("      <a href=\""+ homepath +"?action=clearInterceptors\">Clear Cache</a>&nbsp;");
        buff.append("   </td>");
        buff.append("</tr>");
        buff.append("<tr class=\"filler\"><td>&nbsp;</td></tr>");
        buff.append("<tr>");
        buff.append("   <td class=\"key\">Schema</td>");
        buff.append("   <td class=\"actions\">");
        buff.append("      <a href=\""+ homepath +"?action=clearSchema\">Clear Cache</a>&nbsp;");
        buff.append("      <a href=\""+ req.getContextPath() +"/cache/schema/"+ contextName +"\">View Details</a>");
        buff.append("   </td>");
        buff.append("</tr>");
        buff.append("<tr class=\"filler\"><td>&nbsp;</td></tr>");
        buff.append("<tr>");
        buff.append("   <td class=\"key\">SQL</td>");
        buff.append("   <td class=\"actions\">");
        buff.append("      <a href=\""+ homepath +"?action=clearSql\">Clear Cache</a>&nbsp;");
        buff.append("   </td>");
        buff.append("</tr>");
        buff.append("</table></body></html>");
        
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setDateHeader("Expires", System.currentTimeMillis());
        res.setContentType("text/html");
        try { 
            res.getWriter().println( buff.toString());
        } catch (IOException ex) {
            throw new RuntimeException( ex); 
        }
    }
}
