/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.sql;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dell
 */
public class SqlFunctionProvider {
    
    private static String classPath = "com.rameses.sql.dialect.functions.";
    
    private static Map<String, Class<? extends SqlDialectFunction>> funcMap = Collections.synchronizedMap(new HashMap());
    
    public static SqlDialectFunction getFunction(String funcName, String dialect) {
        String fn = classPath + dialect.toLowerCase() +"."+ funcName.toUpperCase();
        if( !funcMap.containsKey(fn) ) {
            Class clazz = null;
            //attempt to load the class path
            try {
                ClassLoader loader = SqlFunctionProvider.class.getClassLoader();
                clazz = loader.loadClass(fn);
            }
            catch(Exception ign){;}
            funcMap.put(fn,clazz);
        }
        try {
            Class clazz = funcMap.get(fn);
            if(clazz!=null) {
                return (SqlDialectFunction) clazz.newInstance();
            }
        }catch(Exception ign) {;}
        
        //if there are errors and function was not loaded....
        return new SimpleSqlDialectFunction(funcName);
    } 
    
    
}
