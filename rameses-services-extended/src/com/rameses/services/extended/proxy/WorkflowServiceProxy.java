/*
 * WrokflowServiceProxy.java
 *
 * Created on June 27, 2014, 5:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.services.extended.proxy;

import com.rameses.util.ObjectDeserializer;
import groovy.lang.GroovyObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class WorkflowServiceProxy {
    
    private GroovyObject wf;
    private GroovyObject taskDb;
    private String tasktableName;
    private String processName;
    
    /** Creates a new instance of WrokflowServiceProxy */
    public WorkflowServiceProxy(Object w, String tableName, String processName, Object taskDb) {
        this.wf = (GroovyObject)w;
        this.tasktableName = tableName;
        this.processName = processName;
        this.taskDb = (GroovyObject)taskDb;
    }

    public List getOpenForkList(Map parm) {
        parm.put( "taskTablename",tasktableName );
        parm.put( "processname", processName );
        return (List) wf.invokeMethod("getOpenForkList", new Object[]{parm} );
    }
    
     public Map findNodeInfo(  String state ) {
        Map parm = new HashMap();
        parm.put( "state", state );
        parm.put( "processname",processName );
        return (Map)wf.invokeMethod("findNodeInfo", new Object[]{parm} );
    }
    
    public List getTransitionList( String state ) throws Exception {
        if( state == null ) throw new Exception("state is required in WorkflowService.getTransitionList");
        Map pr = new HashMap();
        pr.put("nodename", state);
        pr.put("processname", processName );
        List<Map> transitions = (List)wf.invokeMethod( "getTransitionList", new Object[]{ pr } );
        for(Map x: transitions) {
            String sprop = (String)x.get("properties");
            if( sprop!=null) {
                ObjectDeserializer dr= new ObjectDeserializer();
                Map pmap = (Map)dr.read( sprop );
                x.put("properties", pmap );
            }
        }
        return transitions;
    }
     
    public Map findTask(String taskId) throws Exception {
        Map prm = new HashMap();
        prm.put( "taskTablename",tasktableName );
        prm.put( "processname", processName );
        prm.put( "objid", taskId );
        Map m = (Map)wf.invokeMethod( "findTask", new Object[]{prm}  );
        if(m==null)
            throw new Exception("Cannot find task with id " + taskId);
        return m;
    }
    
    public List getOpenTaskList(String refid) {
        Map parm = new HashMap();
        parm.put("refid", refid);
        parm.put( "taskTablename",tasktableName );
        parm.put( "processname", processName );
        return (List)wf.invokeMethod("getOpenTaskList", new Object[]{parm} );
    }
    
    public List getStates() {
        Map r = new HashMap();
        r.put("processname", processName);
        return (List)wf.invokeMethod( "getStates", new Object[]{r}  );
    }
    
    public void updateTask(Map task) {
        taskDb.invokeMethod("update", new Object[]{task} );
    }
    
    public void createTask(Map task) {
        taskDb.invokeMethod( "create", new Object[]{task} );   
    }
     
    
}
