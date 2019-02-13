/*
 * ScriptInfo.java
 *
 * Created on January 26, 2013, 8:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.classutils.ClassDef;
import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.MainContext;
import groovy.lang.GroovyClassLoader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ScriptInfo {
    
    private String name;
    private URL urlSource;
    private ClassDef classDef;
    private Class clazz;
    private int minPoolSize = 5;
    private int maxPoolSize = 100;
    
    private Class interfaceClass;
    private String stringInterface;
    
    private ScriptExecutorPool pool;
    private AbstractContext sourceContext;
    private Class metaClass;
    private ClassLoader classLoader;
    
    private Map<String, CheckedParameter[]> checkedParameters = Collections.synchronizedMap( new HashMap());
    
    public ScriptInfo(String s, URL u, Class clz, AbstractContext src, ClassLoader classLoader) {
        this.name = s;
        this.urlSource = u;
        this.clazz = clz;
        this.classDef = new ClassDef(clz);
        this.pool = new ScriptExecutorPool(this);
        this.sourceContext = src;
        this.classLoader = classLoader;
    }
    
    public String getName() {
        return name;
    }
    
    public URL getUrlSource() {
        return urlSource;
    }
    
    public ClassDef getClassDef() {
        return classDef;
    }
    
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    public int getMinPoolSize() {
        return minPoolSize;
    }
    
    public ScriptExecutor newInstance() throws Exception {
        return  this.pool.get();
    }
    
    public synchronized Class getInterfaceClass() {
        if(interfaceClass==null) {
            try {
                stringInterface = StringInterface.buildInterface( clazz );
                interfaceClass = ((GroovyClassLoader)sourceContext.getClassLoader()).parseClass( stringInterface );
            } catch(RuntimeException re) {
                re.printStackTrace();
                throw re;
            } catch(Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return interfaceClass;
    }
    
    public synchronized String getStringInterface() {
        if(stringInterface==null) getInterfaceClass();
        return stringInterface;
    }
    
    public AbstractContext getSourceContext() {
        return sourceContext;
    }
    
    public CheckedParameter[] getCheckedParameters(String methodName) {
        if(!checkedParameters.containsKey(methodName)) {
            CheckedParameter[] checkParams = CheckedParameterBuilder.getCheckedParameters( methodName, classDef );
            checkedParameters.put( methodName, checkParams );
        }
        return checkedParameters.get( methodName );
    }
    
    public void destroy() {
        urlSource = null;
        classDef = null;
        clazz = null;
        interfaceClass = null;
        stringInterface = null;
        sourceContext = null;
        metaClass = null;
        pool.destroy();
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    public Map getMetaInfo(MainContext ct) {
        return MetaInfoMapInterface.buildInterface( this.getName(), clazz, ct );
    }
}
