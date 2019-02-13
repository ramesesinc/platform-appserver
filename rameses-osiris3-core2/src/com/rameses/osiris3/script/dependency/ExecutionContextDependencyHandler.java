/*
 * TransactionScopeDependencyHandler.java
 *
 * Created on January 15, 2013, 6:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.dependency;

import com.rameses.annotations.ExecutionContext;
import com.rameses.osiris3.script.DependencyHandler;
import com.rameses.osiris3.script.ExecutionInfo;
import java.lang.annotation.Annotation;

/**
 *
 * @author Elmo
 */
public class ExecutionContextDependencyHandler extends DependencyHandler {
    
    public Class getAnnotation() {
        return ExecutionContext.class;
    }
    public Object getResource(Annotation c, ExecutionInfo e) {
        return e;
    }
    
}
