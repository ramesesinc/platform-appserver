/*
 * TransactionScopeDependencyHandler.java
 *
 * Created on January 15, 2013, 6:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.dependency;

import com.rameses.annotations.Resource;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.script.DependencyHandler;
import com.rameses.osiris3.script.ExecutionInfo;
import com.rameses.osiris3.core.TransactionContext;
import java.lang.annotation.Annotation;

/**
 *
 * @author Elmo
 */
public class ResourceDependencyHandler extends DependencyHandler {
    
    public Class getAnnotation() {
        return Resource.class;
    }
    
    public Object getResource(Annotation c, ExecutionInfo e) {
        Resource res = (Resource)c;
        String value = res.value();
        MainContext ctx = TransactionContext.getCurrentContext().getContext();
        return ctx.getService( value );
    }
    
}
