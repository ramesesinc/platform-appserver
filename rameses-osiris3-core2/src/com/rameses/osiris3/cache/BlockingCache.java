/*
 * BlockingCache.java
 *
 * Created on February 10, 2013, 9:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.cache;

import com.rameses.osiris3.xconnection.XConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Elmo
 */
public abstract class BlockingCache extends XConnection implements CacheConnection {
    
    private ExecutorService thread;
    
    public void start()  {
        thread = Executors.newCachedThreadPool();
    }
    
    public void stop()  {
        thread.shutdownNow();
    }
    
    //this is for a blocking get
    public Object get(final String name, int timeout) throws Exception {
        Object result = get(name);
        if(result!=null) return result;
        Future future = thread.submit( new Callable(){
            public Object call() throws Exception {
                Object result = null;
                while(result==null) {
                    result = get(name);
                    if(result!=null) break;
                    Thread.sleep(2000);
                }
                return result;
            }
        });
        return future.get( timeout, TimeUnit.SECONDS );
    }
    
  
    
}
