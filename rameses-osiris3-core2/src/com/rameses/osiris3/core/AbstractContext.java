/*
 * AbstractContext.java
 *
 * Created on January 26, 2013, 7:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class AbstractContext {
    
    protected OsirisServer server;
    private Map conf;
    private String name;
    private String rootUrl;
    private ClassLoader classLoader;
    private Map<String, ContextResource> resources = Collections.synchronizedMap(new HashMap());
    private TransactionManagerProvider transactionManagerProvider;
    
    
    
    public AbstractContext(OsirisServer s) {
        this.server = s;
    }
    
    public abstract void start();
    
    public void stop() {
        //do nothing
    }
    
    public final void setConf(Map c) {
        this.conf = c;
    }
    
    public final Map getConf() {
        return conf;
    }
    
    public Object getProperty(String name) {
        return conf.get(name);
    }
    
    public Object getProperties() {
        return conf;
    }
    
    public final ClassLoader getClassLoader() {
        return classLoader;
    }
    
    public final void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public final void addResource(Class c, ContextResource s ) {
        resources.put( c.getSimpleName(), s );
    }
    
    public final <T> T getResource(Class<T> clazz, String name ) {
        return (T) resources.get( clazz.getSimpleName() ).getResource(  name );
    }
    
    public ContextResource getContextResource(Class clazz ) {
        return resources.get(clazz.getSimpleName());
    }
    
    public ContextResource getContextResource(String s ) {
        return resources.get(s);
    }
    
    public final String getName() {
        return name;
    }
    
    public final void setName(String name) {
        this.name = name;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public TransactionManagerProvider getTransactionManagerProvider() {
        return transactionManagerProvider;
    }

    public void setTransactionManagerProvider(TransactionManagerProvider transactionManagerProvider) {
        this.transactionManagerProvider = transactionManagerProvider;
    }

    public OsirisServer getServer() {
        return server;
    }

    public final <T> T getConnection( String name ) {
        return (T) getResource( com.rameses.annotations.XConnection.class,  name );
    }
    
}
