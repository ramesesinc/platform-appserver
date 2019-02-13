/*
 * MainContext.java
 *
 * Created on January 30, 2013, 10:12 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

import com.rameses.server.ServerPID;
import com.rameses.util.Service;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Elmo
 * MainContext is the front facing context. Applicable for AppContext
 * and PluginContext. Not applicable to SharedContext
 *
 */
public class MainContext extends AbstractContext {
    
    //this is used for running asynchronous tasks
    private Map<String, ContextService> services = Collections.synchronizedMap(new HashMap());
    protected ExecutorService asyncExecutor = Executors.newCachedThreadPool();
    
    public MainContext(OsirisServer s) {
        super(s);
    }
    
    public void start() {
        try {
            ServerPID.add(getClass().getName()); 
            startImpl(); 
        } catch(Throwable t) {
            t.printStackTrace(); 
        } finally {
            ServerPID.remove(getClass().getName()); 
        }
    }
    
    private void startImpl() {
        asyncExecutor = Executors.newCachedThreadPool();
        //load the services
        List<ContextService> list = new ArrayList();
        Iterator<ContextService> iter = Service.providers(ContextService.class, getClass().getClassLoader());
        while(iter.hasNext()) {
            ContextService cs = iter.next();
            cs.setContext( this );
            addService( cs.getProviderClass(), cs );
            list.add( cs );
        }
        
        Collections.sort( list );
        for(ContextService c: list) {
            try {
               c.start();
            } catch(Exception e) {
                System.out.println("error starting service " + super.getName()+":"+c.getName() +" " +e.getMessage());
            } 
        }
    }
    
    public void stop() {
        try {
            Collection<ContextService> list = services.values();
            //we have to stop it in reverse order
            Stack<ContextService> stack = new Stack();
            for(ContextService c: list) {
                stack.push(c);
            }
            while( !stack.empty() )   {
                ContextService c = null;
                try {
                    c = stack.pop();
                    c.stop();
                } catch(Exception e) {
                    System.out.println("error stopping service " + super.getName()+":"+c.getName() +" " +e.getMessage());
                }
            }
            asyncExecutor.shutdownNow();
            //asyncExecutor.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        //call the abstract class to close resources
        super.stop();
    }
    
    
    public final void addService(String name, ContextService svc) {
        this.services.put( name, svc );
    }
    
    public final void addService(Class serviceClass, ContextService svc) {
        this.services.put( serviceClass.getSimpleName(), svc );
    }
    
    public final ContextService getService(String serviceName) {
        return services.get(serviceName);
    }
    
    public final <T> T getService(Class<T> serviceClass ) {
        return (T)services.get( serviceClass.getSimpleName() );
    }
    
    public void submitAsync(Runnable runnable) {
        asyncExecutor.submit( runnable );
    }
    
}
