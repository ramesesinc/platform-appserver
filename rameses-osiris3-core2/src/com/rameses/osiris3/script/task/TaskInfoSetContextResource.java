/*
 * TaskInfoSetContextResource.java
 *
 * Created on January 30, 2013, 8:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.task;

import com.rameses.annotations.Shutdown;
import com.rameses.annotations.Schedule;
import com.rameses.osiris3.core.ContextResource;
import com.rameses.osiris3.script.ScriptInfo;
import com.rameses.util.URLDirectory;
import com.rameses.util.URLDirectory.URLFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Elmo
 */
public class TaskInfoSetContextResource extends ContextResource  {
    
    private final static String ENV_NAME = "scripts.tasks.enabled";
    
    public void init() {
        //do nothing
    }

    public Class getResourceClass() {
        return TaskInfoSet.class;
    }
    
    public TaskInfoSet findResource(String xname) {
        TaskInfoSet taskInfoSet = new TaskInfoSet();
        try {
            final Set<String> list = new HashSet();
            
            Object env_value = System.getenv().get( ENV_NAME ); 
            if ( env_value == null || env_value.toString().length() == 0 ) {
                env_value = System.getProperties().get( ENV_NAME ); 
            }
            boolean enable_tasks = (
                env_value == null ? true : (
                    env_value.toString().matches("0|false") ? false : true 
                )
            ); 
            if ( enable_tasks ) {
                Enumeration<URL> e = context.getClassLoader().getResources("scripts/tasks");
                if(e!=null) {
                    while(e.hasMoreElements()) {
                        final URL parent = e.nextElement();
                        URLDirectory dir = new URLDirectory(parent);
                        dir.list( new URLFilter() {
                            public boolean accept(URL u, String filter) {
                                list.add( "tasks/"+ filter.substring(filter.lastIndexOf("/")+1)  );
                                return false;
                            }
                        });
                    }
                }
            }
            
            for(String serviceName: list) {
                ScriptInfo sinfo = context.getResource( ScriptInfo.class, serviceName );
                for(Method bm: sinfo.getClassDef().findAnnotatedMethods( Schedule.class )) {
                    Schedule sked = bm.getAnnotation( Schedule.class );
                    TaskInfo tf = new TaskInfo( serviceName, bm.getName(), null, new HashMap() );
                    tf.setFixedInterval( sked.fixedInterval() );
                    tf.setInterval( sked.interval() );
                    tf.setTimeUnit( sked.timeUnit() );
                    tf.setImmediate( sked.immediate() );
                    tf.setId( sked.id() );
                    tf.setIndex(sked.index());
                    taskInfoSet.addTaskInfo( tf );
                }
                //load also on stop tasks
                for(Method bm: sinfo.getClassDef().findAnnotatedMethods( Shutdown.class )) {
                    Shutdown sked = bm.getAnnotation( Shutdown.class );
                    TaskInfo tf = new TaskInfo( serviceName, bm.getName(), null, new HashMap() );
                    tf.setIndex(sked.index());
                    taskInfoSet.addShutdownTask( tf );
                }
            }
            taskInfoSet.sort();
            return taskInfoSet;
            
        } catch(Exception e) {
            e.printStackTrace();
            return taskInfoSet;
        }
    }
}
