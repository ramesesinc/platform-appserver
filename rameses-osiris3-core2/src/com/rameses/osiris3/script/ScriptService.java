/*
 * ScriptService.java
 *
 * Created on January 26, 2013, 8:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.osiris3.core.*;
import com.rameses.util.Service;
import java.util.Iterator;

/**
 *
 * @author Elmo
 */
public class ScriptService extends ContextService {
    
    private DependencyInjector dependencyInjector = new DependencyInjector();
    
    public Class getProviderClass() {
        return ScriptService.class;
    }
    public void start() throws Exception {
        //add the dependency injectors
        Iterator<DependencyHandler> iter = Service.providers( DependencyHandler.class, getClass().getClassLoader() );
        while(iter.hasNext()) {
            addDependencyInjector( iter.next() );
        }
    }
    
    public final int getRunLevel() {
        return 5;
    }
    
    public void stop() throws Exception {
        dependencyInjector.clear();
    }
    
    
    public ScriptInfo findScriptInfo( String name) {
        //if script info cannot be found, select in service provider
        try {
            if( context instanceof AppContext ) {
                SharedContext sctx = ((AppContext)context).getSharedContext();
                try {
                    if (sctx != null) return sctx.getResource(ScriptInfo.class, name);
                } catch(ResourceNotFoundException nfe) {
                    //do nothing, proceed below
                }
                try {
                    return context.getResource( ScriptInfo.class, name );
                } catch(ResourceNotFoundException nfe) {
                }
            } else {
                try {
                    return context.getResource( ScriptInfo.class, name );
                } catch(ResourceNotFoundException ign){;}
            }
            throw new ResourceNotFoundException("resource " + name + " not found");
            //check if in database
            /*
            try {
            ScriptInfoScriptProviderService spe = new ScriptInfoScriptProviderService();
            return spe.findResource( name );
            }
            catch(Exception ign) {
             
            }
             */
        } catch(ResourceNotFoundException nfe) {
            throw nfe;
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public final ScriptExecutor create( String name) throws Exception {
        ScriptInfo sinfo = findScriptInfo( name );
        return sinfo.newInstance();
    }
    
    public void removeScript(String key) {
        if( context instanceof AppContext ) {
            SharedContext sctx = ((AppContext)context).getSharedContext();
            try {
                sctx.getContextResource(ScriptInfo.class).remove(key);
            } catch(ResourceNotFoundException nfe) {
                //do nothing, proceed below
            }
            try {
                context.getContextResource( ScriptInfo.class ).remove(key);
            } catch(ResourceNotFoundException nfe) {
            }
        } else {
            try {
                context.getContextResource( ScriptInfo.class ).remove(key);
            } catch(ResourceNotFoundException ign){;}
        }
    }
    
    
    public DependencyInjector getDependencyInjector() {
        return dependencyInjector;
    }
    
    public void addDependencyInjector(DependencyHandler h) {
        this.dependencyInjector.addHandler( h );
    }
    
    public InterceptorSet findInterceptors(AbstractContext ctx, String name) {
        return ctx.getResource(InterceptorSet.class, name);
    }
    
    
    
}
