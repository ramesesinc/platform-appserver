/*
 * EnvDependencyHandler.java
 *
 * Created on January 15, 2013, 6:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.dependency;

import com.rameses.annotations.Context;
import com.rameses.osiris3.script.DependencyHandler;
import com.rameses.osiris3.script.ExecutionInfo;
import com.rameses.osiris3.core.TransactionContext;
import java.lang.annotation.Annotation;

/**
 *
 * @author Elmo
 */
public class ContextDependencyHandler  extends DependencyHandler{
    
    public Class getAnnotation() {
        return Context.class;
    }
    public Object getResource(Annotation c,  ExecutionInfo e) {
        return TransactionContext.getCurrentContext().getContext();
    }
    
}
