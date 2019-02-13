package com.rameses.osiris3.server;

import javax.servlet.http.HttpServletRequest;


public final  class RequestParser {
    
    private String cluster;
    private String contextName;
    private String serviceName;
    private String methodName;
    
    public RequestParser(HttpServletRequest r) {
        this(r.getPathInfo() , r);
    }

    public RequestParser(String spath, HttpServletRequest r) {
        int firstIndex = spath.indexOf("/",1);
        if(spath.startsWith("/")) spath = spath.substring(1);
        contextName = spath.substring(0, firstIndex-1);
        serviceName = spath.substring( firstIndex, spath.lastIndexOf(".") );
        methodName = spath.substring(spath.indexOf(".")+1);
        
        this.cluster = r.getContextPath();
        if(this.cluster!=null && this.cluster.startsWith("/")) {
            this.cluster = this.cluster.substring(1);
        }
    }
    
    public String getContextName() {
        return contextName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getCluster() {
        return cluster;
    }
    
}