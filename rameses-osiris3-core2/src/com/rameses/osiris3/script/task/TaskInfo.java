/*
 * TaskInfo.java
 *
 * Created on January 10, 2013, 7:14 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.task;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public final class TaskInfo {
    
    private boolean cancelled;
    private int interval = 0;
    private String timeUnit = "SECONDS";
    private boolean fixedInterval = false;
    private String serviceName;
    private String methodName;
    private Object[] args;
    private Map env;
    private Exception exception;
    private boolean immediate;
    private String id;
    private Map properties = new HashMap();
    private int index;
    
    public TaskInfo(String serviceName, String methodName, Object[] args, Map env ) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.args = args;
        this.env = env;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public boolean isFixedInterval() {
        return fixedInterval;
    }

    public void setFixedInterval(boolean fixedInterval) {
        this.fixedInterval = fixedInterval;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public Map getEnv() {
        return env;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }


    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public Map getProperties() {
        return properties;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int hashCode() {
        return (this.serviceName+"."+this.methodName).hashCode();
    }

    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    


    
}
