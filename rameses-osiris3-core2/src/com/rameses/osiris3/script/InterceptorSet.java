/*
 * InterceptorSet.java
 *
 * Created on January 28, 2013, 1:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import java.util.List;

/**
 *
 * @author Elmo
 */
public class InterceptorSet {
    
    private String name;
    private List<InterceptorInfo> beforeInterceptors;
    private List<InterceptorInfo> afterInterceptors;
    
    public InterceptorSet(String name, List<InterceptorInfo> bl, List<InterceptorInfo> al) {
        this.name = name;
        this.beforeInterceptors = bl;
        this.afterInterceptors = al; 
    }
    
    public List<InterceptorInfo> getBeforeInterceptors() {
        return beforeInterceptors;
    }
    
    public List<InterceptorInfo> getAfterInterceptors() {
        return afterInterceptors;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean equals(Object obj) {
        InterceptorSet i = (InterceptorSet)obj;
        return getName().equals( i.getName() );
    }

   

}
