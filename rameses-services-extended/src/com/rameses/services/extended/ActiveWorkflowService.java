/*
 * ActiveWorkflowService.java
 *
 * Created on June 3, 2014, 8:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.services.extended;

import com.rameses.annotations.ActiveDB;
import com.rameses.annotations.Env;
import com.rameses.annotations.ProxyMethod;
import com.rameses.annotations.Service;
import com.rameses.common.ExpressionResolver;
import com.rameses.services.extended.proxy.DateServiceLocalInterface;
import com.rameses.services.extended.proxy.NotificationServiceProxy;
import com.rameses.services.extended.proxy.WorkflowServiceProxy;
import com.rameses.services.extended.proxy.WorkitemServiceProxy;
import com.rameses.util.Base64Cipher;
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
public abstract class ActiveWorkflowService {
    
    @ActiveDB("wf")
    private Object wf;
    
    protected abstract Object getTaskDB();
    protected abstract String getProcessname();
    
    public Object getWorkitemDB() {
        return null;
    }
    
    public Object getNotificationService() {
        return null;
    }
    
    public Object getWf() {
        return wf;
    }
    
    public void setWf(Object wf) {
        this.wf = wf;
    }
    
    protected String getTaskTablename() {
        return getProcessname().toLowerCase()+"_task";
    }
    
    protected String getWorkitemTablename() {
        return getProcessname().toLowerCase()+"_workitem";
    }
    
    private WorkflowServiceProxy getWfProxy() {
        return new WorkflowServiceProxy(getWf(), getTaskTablename(), getProcessname(), getTaskDB());
    }
    
    private WorkitemServiceProxy getWorkitemProxy() {
        return new WorkitemServiceProxy(getWf(), getWorkitemTablename(), getProcessname(), getWorkitemDB());
    }
    
    private NotificationServiceProxy getNotificationServiceProxy() {
        return new NotificationServiceProxy(getNotificationService());
    }
    
    @Service(value="DateService", localInterface=DateServiceLocalInterface.class)
    protected DateServiceLocalInterface dateSvc;
    
    @Env
    protected Map env;
    
    @ProxyMethod
    public Object start( Map r ) throws Exception {
        if( r.get("refid") == null ) throw new Exception("refid is required in WorkflowService.start");
        r.put("nodename", "start");
        r.put("prevtask", new HashMap());
        env.put("data", r.get("data"));
        
        List list = new ArrayList();
        findNextTransition(r, false, list, null);
        for( Object o: list ) {
            notifyTask((Map)o);
            onStartTask(o);
        }
        
        if( list.size() == 0 ) 
            throw new Exception("No workflow task found. Please check the workflow definition");
        return list;
    }
    
    //overridable
    public void onStartTask(Object tsk) {;}
    public void beforeCreateTask(Object o) {;}
    public void afterCreateTask(Object o) {;}
    public void beforeCloseTask(Object o) {;}
    public void afterCloseTask(Object o) {;}
    public void beforeOpenTask(Object o) {;}
    public void afterOpenTask(Object o) {;}
    public void beforeSignal(Object params) {;}
    public void afterSignal(Object result) {;}
    public void afterLoadTask(Object newTask) {;}
    public void onEndTask() {;}
    public void loadWorkitem( Object workitem, Object task ) {;}
    public void loadTransition( Object transition, Object task ) {;}
    public void beforeOpenWorkitem(Object o) {;}
    public void afterOpenWorkitem(Object o) {;}    
    
    public void beforeCloseWorkitem(Object o) {;}
    public void afterCloseWorkitem(Object o) {;}    

    public Object getNotificationMessage( Object o ) {return null;}
    
    public boolean checkTaskOwner( Map task ) {return true; }
    public void notifyTask(Object task) {;}
    
    protected Map createTaskInstance(Map t) throws Exception {
        Map m = new HashMap();
        m.put("objid", "TSK"+new UID());
        m.put("startdate", dateSvc.getServerDate());
        m.put("state", t.get("state"));
        m.put("refid", t.get("refid"));
        m.put("parentprocessid", t.get("parentprocessid"));
        
        //from parameters
        m.put("message", env.get("message"));
        m.put("assignee", env.get("assignee"));
        String state = t.get("state").toString();
        m.putAll( getWfProxy().findNodeInfo(state) );
        beforeCreateTask( m );
        getWfProxy().createTask(m);
        afterCreateTask( m );
        return m;
    }
    
    private Map closeTaskInstance(Map r) throws Exception {
        String taskId = (String) r.get("taskid");
        if(taskId==null)
            throw new Exception("closeNodeInstance error. taskid is required");
        Map t = getWfProxy().findTask( taskId );
        if(t.get("enddate")!=null) throw new Exception("Task has already ended");
        t.put("enddate", dateSvc.getServerDate());
        
        //check first if there are open workitems
        if(getWorkitemDB()!=null ) getWorkitemProxy().checkHasOpenWorkitems(r);
        
        Map actor = new HashMap();
        actor.put("objid", env.get("USERID"));
        actor.put("name", env.get("FULLNAME"));
        actor.put("title", env.get("JOBTITLE"));
        t.put("actor", actor);
        t.put("signature", encodeSignature(r));
        beforeCloseTask(t);
        getWfProxy().updateTask(t);
        afterCloseTask(t);
        return t;
    }
    
    private String encodeSignature(Map r){
        if (r.get("signature") == null)
            return null;
        Map s = new HashMap();
        s.put("taskid", r.get("taskid"));
        s.put("signature", r.get("signature"));
        
        Base64Cipher cipher = new Base64Cipher();
        return cipher.encode(s);
    }
    
    public List getOpenForkList( String parentProcessId, Map currentTask ) throws Exception {
        Map parm = new HashMap();
        parm.put( "parentprocessid",parentProcessId );
        
        List<Map> list = getWfProxy().getOpenForkList(parm);
        if( currentTask.get("salience")!=null) {
            int sal = Integer.parseInt( currentTask.get("salience").toString());
            StringBuilder sb = new StringBuilder();
            //check if current task salience must be greater than existing.
            boolean passed =false;
            for( Map st: list ) {
                //compare the saliences
                int isal = -1;
                if( st.get("salience")!=null ) isal = Integer.parseInt(st.get("salience").toString());
                if( isal < sal ) {
                    if(passed)
                        sb.append(",");
                    else
                        passed = true;
                    sb.append(st.get("title")+"");
                }
            }
            String err = sb.toString();
            if(err.length()>0) {
                throw new Exception("Cannot proceed because the following tasks must be completed: \n"+err);
            }
        }
        return list;
    }
    
    @ProxyMethod
    public List getOpenTaskList( Map parm ) throws Exception {
        if(parm.get("refid")==null) throw new Exception("refid is required in getOpenTaskList");
        String refid = parm.get("refid").toString();
        List<Map> list =getWfProxy().getOpenTaskList(refid);
        
        String state = (String)parm.get("state");
        List mlist = new ArrayList();
        if( state !=null) {
            for(Map m: list) {
                String mstate = (String)m.get("state");
                if( state.equals(mstate) ) {
                    mlist.add( m );
                    break;
                }
            }
        } else {
            for(Map m: list) {
                String ntype = (String)m.get("nodetype");
                if( !ntype.equals("fork")) {
                    //do not include forked states
                    mlist.add( m );
                }
            }
        }
        if(mlist.size()==0) throw new Exception("No open tasks for document with state " + state);
        return mlist;
    }
    
    private void findNextTransition( Map r, boolean fireAll, List collector, String tAction ) throws Exception {
        String nodeName = (String)r.get("nodename");
        if(nodeName==null) nodeName = (String)r.get("state");
        if(nodeName==null) throw new Exception("state or nodename is required for nextTransition");
        
        //this assures only the first transition that matches will be executed. except for forks
        boolean breakTransition = false;
        
        List<Map> transitions = getWfProxy().getTransitionList(nodeName);
        for(Map o : transitions) {
            if( breakTransition ) break;
            if( tAction!=null &&  !tAction.equals(o.get("action"))) continue;
            if(!fireAll) breakTransition = true;
            
            boolean pass = checkEvalExpression(o);
            if(!pass) {
                breakTransition = false;  
                continue;
            }
            
            if( "fork".equals( o.get("tonodetype") )) {
                //create fork instance
                Map z = new HashMap();
                z.put("state", o.get("to"));
                z.put("refid", r.get("refid"));
                z.put("parentprocessid", r.get("parentprocessid"));
                Map p = createTaskInstance( z );
                String forkId = (String)p.get("objid");
                
                //create subsequent fork children
                Map param = new HashMap();
                param.put("nodename", o.get("to"));
                param.put("parentprocessid", forkId);
                param.put( "refid", r.get("refid") );
                findNextTransition(param, true, collector, null);
            } else if( "join".equals(o.get("tonodetype")) ) {
                String parentProcessId = (String)r.get("parentprocessid");
                List pendingList = getOpenForkList( parentProcessId, r );
                if(pendingList.size()==0) {
                    //close the main fork
                    Map z = new HashMap();
                    z.put("taskid", parentProcessId);
                    z.put("message", r.get("message"));
                    closeTaskInstance(z);
                    
                    Map zz = new HashMap();
                    zz.put( "refid", r.get("refid") );
                    zz.put( "nodename", o.get("to") );
                    
                    findNextTransition( zz, false, collector, null );
                }
            } 
            else if( "end".equals(o.get("to"))) {
                onEndTask();
                break;
            } 
            else {
                Map z = new HashMap();
                z.put("refid", r.get("refid"));
                z.put("parentprocessid", r.get("parentprocessid"));
                z.put("state", o.get("to"));
                Map tsk = createTaskInstance( z );
                collector.add( tsk );
            }
        }
    }
    
    private boolean checkEvalExpression(Map o){
        //add an eval
        String eval = (String)o.get("eval");
        if(eval!=null && eval.trim().length() > 0) {
            Map m = new HashMap();
            m.put("data", env.get("data"));
            m.put("env", env);
            return ExpressionResolver.getInstance().evalBoolean(eval, m);
        }
        return true;
    }
    
    @ProxyMethod
    public Map signal( Map r ) throws Exception {
        if( r.get("taskid")==null && r.get("refid")==null )
            throw new Exception("taskid or refid is required in WorkflowService.signal");
        if(r.get("taskid")!=null && r.get("state")==null)
            throw new Exception("Please specify a state");
        if( r.get("taskid")==null)  {
            List openList = this.getOpenTaskList( r );
            if(openList.size()>1) throw new Exception("There are more than 1 open tasks for this document. Please specify a state");
            Map ff = (Map)openList.iterator().next();
            r.put("taskid", ff.get("objid"));
        }
        
        String action = (String)r.get("action");
        env.put("data", r.get("data"));
        env.put("action", r.get("action"));
        env.put("message", r.get("message"));
        env.put("assignee", r.get("assignee"));
        
        beforeSignal(r);
        
        //if there is an assignee, move it out from previous task and place it in the new task
        Map t = closeTaskInstance( r );
        env.put("prevtask", t );
        
        //close the existing task and find the next instance
        Map m = new HashMap();
        m.put("state", t.get("state"));
        m.put("refid", t.get("refid"));
        m.put("taskid", t.get("objid"));
        m.put("parentprocessid", t.get("parentprocessid"));
        m.put("salience", t.get("salience")); //this is very impt. for checking salience
        
        //get possible concurrent tasks
        List<Map> tsks = new ArrayList();
        findNextTransition( m, false, tsks, action );
        Map newTask = null;
        List newTasks = new ArrayList();
        
        /*************************************************************
        * added by Elmo to support business. there is a bug. Only
        * 'owned' tasks by the submitter will be displayed. we want
        * to notify all tasks. We do not want to touch this bec,
        * Jessie is using this, for some reason it seems to be correct for him.
        *****************************************************************/
        List<Map> notifyTasks = new ArrayList();
        for( Map tk : tsks ) {
            if( isTaskOwner(tk) ) {
                newTasks.add(  tk );
                tk.put("taskid", tk.get("objid"));
                if(newTask==null) {
                    newTask = getTaskInfo(tk);
                    newTask.put("owner",true);
                }
            }
            notifyTasks.add(tk);
        }
        
        Map result = new HashMap();
        result.put("tasks", newTasks);
        result.put("task", newTask );
        
        if(newTask!=null) {
            loadTask(newTask);
        }
        afterSignal(result);
        
        //load the task
        for(Map tsk: notifyTasks) {
            notifyTask(tsk);
        }
        
        if( getNotificationService()!=null ) {
            Map msg = (Map)getNotificationMessage(newTask);
            if(msg!=null) {
                if(msg.get("recipientid")==null) throw new Exception("recipientid is required in getNotificationService");
                if(msg.get("recipienttype")==null) throw new Exception("recipienttype is required in getNotificationService");
                if(msg.get("senderid")==null) throw new Exception("senderid is required in getNotificationService");
                if(msg.get("sender")==null) throw new Exception("sender is required in getNotificationService");
                if(msg.get("message")==null) throw new Exception("message is required in getNotificationService");
                if(msg.get("filetype")==null) throw new Exception("filetype is required in getNotificationService");
                try {
                    getNotificationServiceProxy().addMessage(msg);
                }
                catch(Exception e) {
                    System.out.println("Cannot send message due to:" + e.getMessage() + ". message is " + msg );
                }
            }
        }
        return result;
    }
    
    @ProxyMethod
    public Map addWorkitem( Map t ) throws Exception {
        if( getWorkitemDB()==null ) throw new Exception("workitemDb not defined");
        if( t.get("taskid") == null ) throw new Exception("taskid is required in WorkflowService.addWorkitem");
        String taskid = t.get("taskid").toString();
        //check first if task is already closed, you cannot add a workitem to it.
        Map tsk = getWfProxy().findTask( taskid );
        if(tsk.get("enddate")!=null) throw new Exception("Task has already ended");
        return  getWorkitemProxy().createWorkitem( t,  (Date) dateSvc.getServerDate() );
    }
    
    @ProxyMethod
    public Map openWorkitem( Map r ) throws Exception {
        beforeOpenWorkitem(r);
        Map wi = getWorkitemProxy().openWorkitem(r);
        afterOpenWorkitem(wi);
        return wi;
    }
    
    @ProxyMethod
    public Map closeWorkitem(Map r) throws Exception {
        if( getWorkitemDB()==null ) throw new Exception("WorkitemDb not defined");
        beforeCloseWorkitem(r);
        if( r.get("objid") == null ) throw new Exception("objid is required in WorkflowService.closeWorkitem");
        Map actor = new HashMap();
        actor.put("objid", env.get("USERID"));
        actor.put("name", env.get("FULLNAME"));
        actor.put("title", env.get("JOBTITLE"));
        Map w = (Map) getWorkitemProxy().closeWorkItem( r,(Date)dateSvc.getServerDate(),actor );
        afterCloseWorkitem(w);
        return w;
    }
    
    
    @ProxyMethod
    public List getStates() {
        return getWfProxy().getStates();
    }
    
    
    /****
     * openTask is the one that is hooked on.
     */
    @ProxyMethod
    public Map openTask( Map map ) throws Exception {
        beforeOpenTask(map);
        Map t = getTaskInfo(map);
        isTaskOwner(t);
        afterOpenTask(t);
        
        //place data in env;
        env.put("data", t.get("data"));
        
        loadTask(t);
        return t;
    }
    
    private void loadTask( Map task ) throws Exception {
        //check assignees. if a task has assignee, do not display
        Map data =(Map)env.get("data");
        Map prevTask = (Map)env.get("prevtask");
         String state = task.get("state").toString();
        
        List<Map> transitions = getWfProxy().getTransitionList(state);
        List xtransitions = new ArrayList();
        if( transitions.size() > 0 ) {
            for(Map m: transitions) {
                boolean pass = checkEvalExpression(m);
                if(pass) {
                    loadTransition( m, task );
                    xtransitions.add(m);
                }
            }
        }
        task.put("transitions", xtransitions );
        
        //attach also workitem types
        if( getWorkitemDB()!=null ) {
            Map parm = new HashMap();
            parm.put("data", data);
            parm.put("task", task);
            parm.put("prevtask", prevTask);
            List<Map> workitemTypes = getWorkitemProxy().getWorkitemTypes(state, parm);
            for(Map m: workitemTypes) {
                loadWorkitem( m, task );
            }
            task.put("workitemtypes", workitemTypes );
        }
        afterLoadTask( task );
    }
    
    
    private Map getTaskInfo( Map map ) throws Exception {
        if(map.get("taskid") == null) throw new Exception("taskid is required in getTaskInfo");
        String taskId = map.get("taskid").toString();
        return getWfProxy().findTask(taskId);
    }
    
    @ProxyMethod
    public List getOpenTasks( Map map ) throws Exception {
        if(map.get("refid") == null) throw new Exception("refid is required in getTasks");
        map.put("objid", map.get("refid"));
        List<Map> tsks = getOpenTaskList(map);
        List tskList = new ArrayList();
        Map parm = new HashMap();
        for(Map t: tsks) {
            if( isTaskOwner(t)) {
                parm.put( "taskid", t.get("objid") );
                Map eTsk = getTaskInfo( parm );
                eTsk.put("owner", true);
                tskList.add( eTsk );
            }
        }
        return tskList;
    }
    
    
    //overridable
    
    private boolean isTaskOwner( Map task ) throws Exception {
        String userId = (String) env.get("USERID");
        if( userId == null )
            throw new Exception("USERID is null. Please check if you have logged in");
        Map assignee = (Map)task.get("assignee");
        if(assignee!=null && assignee.get("objid") != null) {
            String assigneeId = (String)assignee.get("objid");
            if(userId.equals(assigneeId)) {
                task.put("owner", true);
                return true;
            }
            else {
                task.put("owner",false);
                return false;
            }
        }
        boolean test = checkTaskOwner( task );
        if(test==true) {
            task.put("owner", true);
            return true;
        }
        task.put("owner", false);
        return false;
    }
    
}
