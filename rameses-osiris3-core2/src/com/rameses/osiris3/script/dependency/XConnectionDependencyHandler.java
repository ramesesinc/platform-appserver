/*
 * TransactionScopeDependencyHandler.java
 *
 * Created on January 15, 2013, 6:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.dependency;

import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.script.DependencyHandler;
import com.rameses.osiris3.script.ExecutionInfo;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionFactory;
import java.lang.annotation.Annotation;

/**
 *
 * @author Elmo
 */
public class XConnectionDependencyHandler extends DependencyHandler {
    
    public Class getAnnotation() {
        return com.rameses.annotations.XConnection.class;
    }
    
    public class DynamicXConnection {
        private MainContext txnCtx;
        public DynamicXConnection(  MainContext ctx ) {
            this.txnCtx = ctx;
        }
        public Object lookup(String connName) throws Exception {
            if(connName==null) throw new Exception("Connection name is required in DynamicXConnection.lookup");
            XConnection xconn = txnCtx.getResource( XConnection.class, connName );
            if (xconn instanceof XConnectionFactory) {
                XConnectionFactory factory = (XConnectionFactory) xconn;
                String category = factory.extractCategory( connName ); 
                if (category==null || category.length()==0) {
                    //do nothing 
                } else {
                    xconn = factory.getConnection(category); 
                } 
            } 
            return xconn;
        }
    }
    
    
    public Object getResource(Annotation c, ExecutionInfo e) {
        com.rameses.annotations.XConnection mc = (com.rameses.annotations.XConnection)c;
        MainContext ctx = TransactionContext.getCurrentContext().getContext();
        
        if( mc.dynamic() == true ) {
            return new XConnectionDependencyHandler.DynamicXConnection( ctx );
        }
        
        String connName = mc.value();
        if (connName==null || connName.trim().length()==0) {
            connName = "default";
        }   
        XConnection xconn = ctx.getResource( XConnection.class, connName );
        if (xconn instanceof XConnectionFactory) {
            XConnectionFactory factory = (XConnectionFactory) xconn;
            String category = factory.extractCategory( connName ); 
            if (category==null || category.length()==0) {
                //do nothing 
            } else {
                xconn = factory.getConnection(category); 
            } 
        } 
        return xconn; 
    }
    
}
