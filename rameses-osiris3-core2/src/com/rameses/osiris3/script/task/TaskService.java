/*
 * TaskService.java
 *
 * Created on January 7, 2013, 4:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.task;

import com.rameses.osiris3.core.*;
import com.rameses.osiris3.script.ScriptRunnable;
import com.rameses.osiris3.script.ScriptRunnableListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Elmo
 */
public class TaskService extends ContextService {
    
    private static String TIMEUNIT_SECONDS = "SECONDS";
    private static String TIMEUNIT_MINUTES = "MINUTES";
    private static String TIMEUNIT_HOURS = "HOURS";
    private static String TIMEUNIT_DAYS = "DAYS";
    
    private boolean started;
    private ScheduledExecutorService taskScheduler = Executors.newScheduledThreadPool(100);
    private List<TaskInfo> tasks;
    
    private Map<String, TimeUnit> timeUnits = new HashMap();
    
    private Set<TaskInfo> failedTasks = new CopyOnWriteArraySet();
    private ExecutorService asyncPool = Executors.newFixedThreadPool(100);
    
    
    //new addition. Particularly used for queues
    private List<TaskInfo> shutdownTasks = new LinkedList();
    
    public Class getProviderClass() {
        return TaskService.class;
    }
    
    public final int getRunLevel() {
        return 30;
    }
    
    /***************************************************************************
     * initialize manager
     **************************************************************************/
    
    
    public final void start() throws Exception {
        if( started ) return;
        HashSet<TaskInfo> set = new HashSet();
        if(context instanceof AppContext) {
            SharedContext sc = ((AppContext)context).getSharedContext();
            if(sc!=null) {
                TaskInfoSet ts = sc.getResource( TaskInfoSet.class, null );
                set.addAll(ts.getTaskInfos());
                shutdownTasks.addAll( ts.getShutdownTasks() );
            }
        }
        TaskInfoSet ts = context.getResource( TaskInfoSet.class, null );
        set.addAll( ts.getTaskInfos() );
        shutdownTasks.addAll( ts.getShutdownTasks() );
        for(TaskInfo tf: set) {
            addTask(tf);
        }
        started = true;
    }
    
    public final void stop() throws Exception {
        System.out.println("Executing Task Shutdown");
        for(TaskInfo tf: shutdownTasks) {
            try {
                ScriptRunnable tr = new ScriptRunnable(context);
                tr.setServiceName( tf.getServiceName() );
                tr.setMethodName( tf.getMethodName() );
                tr.setArgs( new Object[]{tf} );
                tr.setEnv( tf.getEnv() );
                tr.run();
            }
            catch(Exception e) {
                System.out.println("Error shutdown. " + e.getMessage());
            }
        }
        taskScheduler.shutdownNow();
        started = false;
    }
    
    /**************************************************************************/
    
    public Future invokeLater( Runnable runnable ) {
        return taskScheduler.submit(runnable);
    }
    
    
    public Future addTask( final TaskInfo tf ) {
        ScriptRunnable tr = new ScriptRunnable(context);
        tr.setServiceName( tf.getServiceName() );
        tr.setMethodName( tf.getMethodName() );
        tr.setArgs( new Object[]{tf} );
        tr.setEnv( tf.getEnv() );
        tr.setListener( new ScriptHandler(tr, tf) );
        
        long interval = tf.getInterval();
        //recompute the time
        if(  tf.getTimeUnit().equalsIgnoreCase(TIMEUNIT_MINUTES) ) {
            interval = interval * 60;
        } else if(  tf.getTimeUnit().equalsIgnoreCase(TIMEUNIT_HOURS) ) {
            interval = interval * 60 * 60;
        } else if(  tf.getTimeUnit().equalsIgnoreCase(TIMEUNIT_DAYS) ) {
            interval = interval * 60 * 60 * 24;
        }
        long initial = (tf.isImmediate()) ? 0 : interval;
        String timeUnit = tf.getTimeUnit();
        boolean fixedInterval = tf.isFixedInterval();
        
        if(interval == 0 )
            return taskScheduler.submit(tr);
        else if( fixedInterval == true ) {
            return taskScheduler.scheduleAtFixedRate( tr, initial, interval,TimeUnit.SECONDS );
        } else {
            return taskScheduler.scheduleWithFixedDelay( tr, initial, interval,TimeUnit.SECONDS );
        }
    }
    
    
    public Set<TaskInfo> getFailedTasks() {
        return failedTasks;
    }
    
    public TaskInfo recover(String id) {
        TaskInfo forRemoval = null;
        for( TaskInfo t: failedTasks ) {
            if(id.equals( t.getId() )) {
                forRemoval = t;
                break;
            }
        }
        if(forRemoval!=null) {
            failedTasks.remove( forRemoval );
            forRemoval.setCancelled( false );
            return forRemoval;
        } else {
            return null;
        }
    }
    
    private class ScriptHandler extends ScriptRunnableListener {
        
        private ScriptRunnable script;
        private TaskInfo taskinfo; 
        
        ScriptHandler(ScriptRunnable script, TaskInfo taskinfo) {
            this.taskinfo = taskinfo;
            this.script = script;            
        }
        
        public void onCommit() { 
            //System.out.println("committing task data!"); 
        } 
        
        public void onRollback(Exception e) { 
            taskinfo.setException( e ); 
            taskinfo.setCancelled(true); 
            getFailedTasks().add(taskinfo); 
            System.out.println("ERROR EXECUTING TASK ->" + e.getMessage() ); 
        } 
        
        public void onComplete(Object result) { 
//            if (result instanceof AsyncRequest) { 
//                ScriptRunnable sr = script.copy(); 
//                sr.setListener( new ScriptHandler(sr, taskinfo) );                
//                sr.setBypassAsync(true);  
//                asyncPool.submit( sr ); 
//            } 
        } 
    } 
} 
