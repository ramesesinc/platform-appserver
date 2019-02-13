/*
 * ScriptInfoServiceResource.java
 *
 * Created on January 27, 2013, 8:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.annotations.After;
import com.rameses.annotations.Async;
import com.rameses.annotations.Before;
import com.rameses.classutils.AnnotationMethod;
import com.rameses.classutils.ClassDef;
import com.rameses.osiris3.core.*;
import com.rameses.util.URLDirectory;
import com.rameses.util.URLDirectory.URLFilter;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Elmo
 * This is per context.
 */
public class InterceptorSetContextResource extends ContextResource {
    
    private List<InterceptorInfo> beforeInterceptors;
    private List<InterceptorInfo> afterInterceptors;
    private boolean initialized;
    
    
    public void init() {
    }
    
    
    protected synchronized Set<String> getInterceptorNames() {
        InputStream is = null;
        final Set<String>  set = new HashSet();
        try {
            Enumeration<URL> e = context.getClassLoader().getResources("scripts/interceptors");
            if(e!=null) {
                while(e.hasMoreElements()) {
                    final URL parent = e.nextElement();
                    URLDirectory dir = new URLDirectory(parent);
                    dir.list( new URLFilter() {
                        public boolean accept(URL u, String filter) {
                            String nm = "interceptors/" + filter.substring(filter.lastIndexOf("/")+1);
                            set.add( nm );
                            return false;
                        }
                    });
                }
            }
            return set;
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {is.close();} catch(Exception ign){;}
        }
    }
    
    private synchronized void loadInterceptors() {
        if(initialized) return;
        
        beforeInterceptors  = new ArrayList();
        afterInterceptors   = new ArrayList();
        
        Set<String> set = getInterceptorNames();
        
        for( String sname: set ) {
            ScriptInfo info = context.getResource( ScriptInfo.class,sname );
            ClassDef classDef = info.getClassDef();
            for(AnnotationMethod m: classDef.getAnnotatedMethods()) {
                Annotation a = m.getAnnotation();
                if(a.annotationType() == Before.class) {
                    Before ba = (Before)a;
                    InterceptorInfo inf =  new InterceptorInfo(info.getName(), m.getMethod().getName() );
                    inf.setEval( ba.eval() );
                    inf.setExclude( ba.exclude() );
                    inf.setIndex( ba.index() );
                    inf.setPattern( ba.pattern() );
                    if(m.getMethod().isAnnotationPresent(Async.class)) {
                        inf.setAsync(true);
                    }
                    beforeInterceptors.add( inf  );
                } else if(a.annotationType() == After.class) {
                    After aa = (After)a;
                    InterceptorInfo inf =  new InterceptorInfo(info.getName(), m.getMethod().getName() );
                    inf.setEval( aa.eval() );
                    inf.setExclude( aa.exclude() );
                    inf.setIndex( aa.index() );
                    inf.setPattern( aa.pattern() );
                    if(m.getMethod().isAnnotationPresent(Async.class)) {
                        inf.setAsync(true);
                    }
                    afterInterceptors.add( inf  );
                }
            }
        }
        initialized = true;
    }
    
    
    public Class getResourceClass() {
        return InterceptorSet.class;
    }

    public InterceptorSet findResource(String name) {
        //if the requesting Context is an AppContext, include the shared also
        Set<InterceptorInfo> _before = new HashSet();
        Set<InterceptorInfo> _after = new HashSet();
        if( context instanceof AppContext ) {
            SharedContext sc = ((AppContext)context).getSharedContext();
            if(sc!=null) {
                InterceptorSet mis = sc.getResource( InterceptorSet.class, name );
                _before.addAll( mis.getBeforeInterceptors() );
                _after.addAll( mis.getAfterInterceptors() );
            }
        }
        
        if(!initialized ) {
            loadInterceptors();
        }
        
        for( InterceptorInfo info : beforeInterceptors ) {
            if(!name.matches( info.getPattern() )) continue;
            if(name.matches( info.getExclude())) continue;
            _before.add(info);
        }
        for( InterceptorInfo info : afterInterceptors ) {
            if(!name.matches( info.getPattern() )) continue;
            if(name.matches( info.getExclude())) continue;
            _after.add(info);
        }
        
        List<InterceptorInfo> _beforeList = new ArrayList();
        _beforeList.addAll( _before );
        _before.clear();
        Collections.sort( _beforeList );
        
        List<InterceptorInfo> _afterList = new ArrayList();
        _afterList.addAll( _after );
        _after.clear();
        Collections.sort( _afterList );
        
        return new InterceptorSet(name, _beforeList, _afterList );
    }
    
    public void remove(String name) {
        beforeInterceptors.clear();
        afterInterceptors.clear();
        super.remove(name);
    }

    
}
