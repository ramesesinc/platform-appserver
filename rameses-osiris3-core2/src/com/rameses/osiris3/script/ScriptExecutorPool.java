/*
 * ScriptExecutorPool.java
 *
 * Created on January 16, 2013, 12:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Elmo
 */
public class ScriptExecutorPool {
    
    private ScriptInfo info;
    private Queue<ScriptExecutor> pool = new LinkedBlockingQueue();
    private int activeInstances = 0;
    
    /** Creates a new instance of ScriptPool */
    ScriptExecutorPool(ScriptInfo info) {
        this.info = info;
    }
    
    public void init() throws Exception {
        Class clazz = this.info.getClassDef().getSource();
        for(int i=0;i< info.getMinPoolSize();i++) {
            ScriptExecutor se = new ScriptExecutor(clazz.newInstance(), info, this);
            pool.add( se );
        }
    }
    
    public ScriptExecutor get() throws Exception {
        activeInstances++;
        ScriptExecutor se = pool.poll();
        if(se!=null) {
            return se;
        } else {
            Class clazz = this.info.getClassDef().getSource();
            se =  new ScriptExecutor(clazz.newInstance(), info, this);
            return se;
        }
    }
    
    void returnToPool(ScriptExecutor se) {
        activeInstances--;
        if( pool.size() > info.getMaxPoolSize() ) {
            se.destroy();
            return;
        } else {
            pool.add( se );
        }
    }
    
    public void destroy() {
        ScriptExecutor se = null;
        while( (se=pool.poll())!=null ) {
            se.destroy();
        }
    }
}
