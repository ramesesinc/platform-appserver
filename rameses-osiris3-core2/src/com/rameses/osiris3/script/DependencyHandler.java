/*
 * ResourceHandler.java
 *
 * Created on January 10, 2013, 7:33 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.annotations.Env;
import com.rameses.annotations.TransactionContext;
import com.rameses.osiris3.core.*;
import java.lang.annotation.Annotation;

/**
 *
 * @author Elmo
 */
public abstract class DependencyHandler {
    
    public abstract Class getAnnotation();
    public abstract Object getResource( Annotation c, ExecutionInfo e );
    
}
