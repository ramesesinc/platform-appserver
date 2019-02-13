/*
 * DependencyInjector.java
 *
 * Created on January 25, 2013, 3:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.classutils.AnnotationField;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class DependencyInjector {
    
    private Map<Class,DependencyHandler> handlers = Collections.synchronizedMap(new HashMap());
    
    public void addHandler(DependencyHandler h) {
        handlers.put( h.getAnnotation(), h );
    }
    
    public void injectDependencies(ScriptExecutor executor, ExecutionInfo e ) {
        ScriptInfo scriptInfo = executor.getScriptInfo();
        for( AnnotationField f: scriptInfo.getClassDef().getAnnotatedFields() ) {
            DependencyHandler h = handlers.get( f.getAnnotation().annotationType() );
            if( h!=null ) {
                Object resource = h.getResource( f.getAnnotation(), e );
                executor.setProperty( f.getField().getName(), resource );
            }
        }
    }
    
    public void clear() {
        handlers.clear();
    }
    
}
