/*
 * StringInterface.java
 *
 * Created on January 23, 2013, 8:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.osiris3.core.MainContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 * There must be at least one method that exists otherwise return null;
 */
public class MetaInfoMapInterface {
    
   
    public static Map buildInterface( String serviceName, Class clazz, MainContext ct ) {
        Map map = new HashMap();
        map.put("serviceName", serviceName);
        XHandler h = new XHandler(map, ct);
        ClassParser.parse( clazz, h  );
        return map;
    }
    
    static class XHandler implements ClassParser.Handler {
        private Map map;
        private boolean hasMethod = false;
        private MainContext ct;
        private String serviceName;
        private ScriptService scriptSvc;
        
        public XHandler(Map map, MainContext ct) {
            serviceName = (String)map.get("serviceName");
            this.map = map;
            this.ct = ct;
            this.scriptSvc = ct.getService(ScriptService.class);
        }
        
        public void start(Class c) {
            map.put("methods", new HashMap() );
        }
        
        private void buildMethod( String methodName, Class[] paramTypes, Class returnType ) {
            Map method = new HashMap();
            method.put("name", methodName);
            if(returnType!=null) method.put( "returnValue", returnType.getName() );
            List params = new ArrayList();
            method.put("parameters", params );
            
            int p = 0;
            for(int i=0; i<paramTypes.length; i++ ) {
                params.add( paramTypes[i].getName() );
            }
            InterceptorSet ints = scriptSvc.findInterceptors(ct, serviceName+"."+methodName );
            List blist = new LinkedList();
            for(InterceptorInfo f: ints.getBeforeInterceptors()) {
                blist.add( f.getServiceName()+"."+f.getMethodName());
            }
            List alist = new LinkedList();
            for(InterceptorInfo f: ints.getAfterInterceptors()) {
                alist.add( f.getServiceName()+"."+f.getMethodName());
            }
            method.put("beforeInterceptors", blist);
            method.put("afterInterceptors", alist);
            ((Map)map.get("methods")).put(methodName, method);
        }
        
        
        public void handleMethod(String methodName, Class[] paramTypes, Class returnType, boolean async) {
            hasMethod = true;
            buildMethod( methodName, paramTypes, returnType );
        }
        
        public void end() {
            
        }
        
    }
    
}
