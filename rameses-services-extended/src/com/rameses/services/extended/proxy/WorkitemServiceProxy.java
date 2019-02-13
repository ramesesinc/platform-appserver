/*
 * WorkitemServiceProxy.java
 *
 * Created on June 27, 2014, 5:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.services.extended.proxy;

import com.rameses.common.ExpressionResolver;
import groovy.lang.GroovyObject;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class WorkitemServiceProxy {
    
    private String tablename;
    private String processName;
    private GroovyObject serviceProxy;
    private GroovyObject workitemDb;
    
    public WorkitemServiceProxy(Object svc, String tableName, String processName, Object workitemDb) {
        this.serviceProxy = (GroovyObject)svc;
        this.tablename = tableName;
        this.processName = processName;
        this.workitemDb = (GroovyObject) workitemDb;
    }
    
    public void checkHasOpenWorkitems(Map task) throws Exception {
        Map map = new HashMap();
        map.put("workitemTablename", tablename);
        map.put("taskid", task.get("taskid"));
        List openWorkitems = (List)serviceProxy.invokeMethod( "getOpenWorkitemList", new Object[]{map});
        if(openWorkitems.size()>0)
                throw new Exception("There are still open work items that need to be completed");        
    }
     
    public List getWorkitemTypes( String state, Map evalObjs ) throws Exception {
        if(state==null) throw new Exception("state is required");
        Map map = new HashMap();
        map.put("processname", processName );
        map.put("state", state);
        List<Map> workitemTypes = (List)serviceProxy.invokeMethod("getWorkitemTypes", new Object[]{map} );
        List xworkitemTypes = new ArrayList();
        for(Map m: workitemTypes) {
            boolean addIt = true;
            String expr = (String)m.get("expr");
            if(expr !=null) {
                try {addIt = ExpressionResolver.getInstance().evalBoolean( expr, evalObjs );} catch(Exception e){;}
            }
            if(addIt) {
                xworkitemTypes.add( m );
            }
        }
        return xworkitemTypes;
    }
    
    public List getOpenWorkitemList( Map parm ) throws Exception {
        parm.put( "workitemTablename",tablename );
        parm.put( "processname", processName );
        return (List)serviceProxy.invokeMethod("getOpenWorkitemList", new Object[]{parm} );
    }
    
    public Map createWorkitem( Map t, Date d ) throws Exception {
        if( t.get("assignee") == null ) throw new Exception("assignee is required in WorkflowService.addWorkitem");
        Map m = new HashMap();
        m.put("objid", "WFST"+new UID());
        m.put("taskid", t.get("taskid"));
        m.put("refid", t.get("refid"));
        m.put("workitemid", t.get("workitemid"));
        m.put("action", t.get("action"));
        m.put("message", t.get("message"));
        m.put("startdate",d);
        m.put("assignee", t.get("assignee"));
        return (Map)workitemDb.invokeMethod( "create", new Object[]{  m } );
    }
    
    public Map closeWorkItem( Map r, Date d, Map actor ) throws Exception {
        Map t = (Map)workitemDb.invokeMethod( "read", new Object[]{r}  );
        if(t.get("enddate")!=null) throw new Exception("workitem is already closed");
        t.put("enddate", d);
        t.put("remarks", r.get("remarks"));
        t.put("actor", actor);
        return (Map)workitemDb.invokeMethod("update", new Object[]{t} );
    }
    
    public Map openWorkitem(Map r) throws Exception {
        return (Map)workitemDb.invokeMethod( "read", new Object[]{r}  );
    }
    
}
