/*
 * ContextService.java
 *
 * Created on January 30, 2013, 9:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

/**
 *
 * @author Elmo
 */
public abstract class ContextService implements Comparable {
    
    protected OsirisServer server;
    protected MainContext context;
    
    public abstract void start() throws Exception;
    public abstract void stop() throws Exception;
    public abstract Class getProviderClass();
    
    public final void setContext(MainContext ctx) {
        this.context = ctx;
        this.server = context.getServer();
    }
    
    
    public String getName() {
        return getClass().getSimpleName();
    }
    
    /**
     * the lower the run level, the earlier it will run. This is defaulted at 10
     * for extended services
     */
    public int getRunLevel() {
        return 10;
    }
    
    public int compareTo(Object o) {
        ContextService mgmt = (ContextService)o;
        if( this.getRunLevel()> mgmt.getRunLevel()  ) {
            return 1;
        } else if(this.getRunLevel()<mgmt.getRunLevel()) {
            return -1;
        } else {
            return 0;
        }
    }
    
}
