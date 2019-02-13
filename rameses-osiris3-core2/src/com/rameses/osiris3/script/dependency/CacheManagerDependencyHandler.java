/*
 * EnvDependencyHandler.java
 *
 * Created on January 15, 2013, 6:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.dependency;

import com.rameses.annotations.Cache;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.script.DependencyHandler;
import com.rameses.osiris3.script.ExecutionInfo;
import com.rameses.osiris3.xconnection.XConnection;
import java.lang.annotation.Annotation;

/**
 *
 * @author Elmo
 */
public class CacheManagerDependencyHandler  extends DependencyHandler{
    
    public Class getAnnotation() {
        return Cache.class;
    }
    public Object getResource(Annotation c,  ExecutionInfo e) {
        String name = ((Cache)c).value();
        if(name==null||name.trim().length()==0) name = com.rameses.osiris3.cache.CacheConnection.CACHE_KEY;
        TransactionContext ct = TransactionContext.getCurrentContext();
        return ct.getContext().getResource( XConnection.class, name );
    }
    
}
