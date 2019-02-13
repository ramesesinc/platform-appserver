/*
 * InterceptorInfo.java
 *
 * Created on January 26, 2013, 8:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

/**
 *
 * @author Elmo
 */
public class InterceptorInfo  implements Comparable{
    
    private String serviceName;
    private String methodName;
    private int index;
    private String eval;
    private String exclude;
    private String pattern;
    private boolean async;
    
    /** Creates a new instance of InterceptorInfo */
    public InterceptorInfo(String serviceName, String methodName) {
        this.serviceName = serviceName;
        this.methodName = methodName;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public int compareTo(Object o) {
        InterceptorInfo m = (InterceptorInfo)o;
        if( this.index > m.index) {
            return 1;
        } else if(this.index< m.index ) {
            return -1;
        } else {
            return 0;
        }
    }
    
    public String getEval() {
        return eval;
    }
    
    public void setEval(String eval) {
        this.eval = eval;
    }
    
    public String getExclude() {
        return exclude;
    }
    
    public void setExclude(String exclude) {
        this.exclude = exclude;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    public String toString() {
        return this.serviceName+"."+this.methodName;
    }

    public int hashCode() {
        return toString().hashCode();
    }
    

    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }
}