/*
 * StringInterface.java
 *
 * Created on January 23, 2013, 8:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.common.AsyncHandler;

/**
 *
 * @author Elmo
 * There must be at least one method that exists otherwise return null;
 */
public class StringInterface {
    
    public static String buildInterface( Class clazz ) {
        XHandler h = new XHandler();
        ClassParser.parse( clazz, h  );
        return h.toString();
    }
    
    static class XHandler implements ClassParser.Handler {
        private StringBuilder sb = new StringBuilder();
        private boolean hasMethod = false;
        
        public void start(Class c) {
            sb.append( "interface " + c.getSimpleName()  + "Intf {\n");
        }
        
        private void buildMethod( String methodName, Class[] paramTypes, Class returnType, boolean async ) {
            if(returnType==null)
                sb.append("  void ");
            else
                sb.append( "  " + returnType.getName() );
            sb.append( " " + methodName + "(");
            
            int p = 0;
            for(int i=0; i<paramTypes.length; i++ ) {
                if(i>0) sb.append(",");
                sb.append( " " + paramTypes[i].getName() + " p"+i  );
            }
            if(async) {
                if(paramTypes.length>0) sb.append(",");
                sb.append( AsyncHandler.class.getName() + " handler" );
            }
            sb.append(") ;\n");
        }
        
        
        public void handleMethod(String methodName, Class[] paramTypes, Class returnType, boolean async) {
            hasMethod = true;
            buildMethod( methodName, paramTypes, returnType, false );
            buildMethod( methodName, paramTypes, returnType, true );
        }
        
        public void end() {
            sb.append("}");
        }
        
        public String toString() {
            if(!hasMethod)
                return null;
            else {
                return sb.toString();
            }    
        }
    }
    
}
