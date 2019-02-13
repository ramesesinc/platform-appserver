/*
 * XConnectionProvider.java
 *
 * Created on February 24, 2013, 9:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import com.rameses.osiris3.core.AbstractContext;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class XConnectionProvider {
    
    protected AbstractContext context;
    
    public void setContext(AbstractContext c) {
        context = c;
    }
    
    public abstract String getProviderName();
    public abstract XConnection createConnection(String name, Map conf);
    
    
//    public Object resolveConfValue(Object value) {
//        if (value == null) return value;
//        
//        Map appconf = (context == null? null: context.getConf()); 
//        if (appconf == null) return value;
//                         
//        int startidx = 0; 
//        boolean has_expression = false; 
//        String str = value.toString(); 
//        StringBuilder builder = new StringBuilder(); 
//        while (true) {
//            int idx0 = str.indexOf("${", startidx);
//            if (idx0 < 0) break;
//            
//            int idx1 = str.indexOf("}", idx0); 
//            if (idx1 < 0) break;
//            
//            has_expression = true; 
//            String skey = str.substring(idx0+2, idx1); 
//            builder.append(str.substring(startidx, idx0)); 
//            
//            Object objval = appconf.get(skey); 
//            if ("app.name".equals(skey)) {
//                objval = (context==null? null: context.getName());
//            }
//            if (objval == null) objval = System.getProperty(skey);             
//            
//            if (objval == null) { 
//                builder.append(str.substring(idx0, idx1+1)); 
//            } else { 
//                builder.append(objval); 
//            } 
//            startidx = idx1+1; 
//        } 
//        
//        if (has_expression) {
//            builder.append(str.substring(startidx));  
//            return builder.toString(); 
//        } else {
//            return value; 
//        } 
//    } 
    
   
    
}
