/*
 * PersistenceContextDependencyHandler.java
 *
 * Created on January 10, 2013, 1:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.dependency;

import com.rameses.annotations.PersistenceContext;
import com.rameses.osiris3.data.DataService;
import com.rameses.osiris3.script.DependencyHandler;
import com.rameses.osiris3.script.ExecutionInfo;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.schema.SchemaManager;

import java.lang.annotation.Annotation;

/**
 *
 * @author Elmo
 */
public class PersistenceContextDependencyHandler extends DependencyHandler {
    
    public Class getAnnotation() {
        return PersistenceContext.class;
    }
    
    public Object getResource(Annotation c, ExecutionInfo einfo) {
        PersistenceContext p = (PersistenceContext)c;
        TransactionContext txn = TransactionContext.getCurrentContext();
        if( p.value().trim().length() > 0 ) {
            DataService dataSvc = txn.getContext().getService(DataService.class);
            return dataSvc.getEntityManager( p.value() );
        } else {
            return new  DynamicPersistenceContext(txn);
        }
    }
    
    public static class DynamicPersistenceContext {
        private TransactionContext txn;
        
        public DynamicPersistenceContext( TransactionContext ctx ) {
            this.txn = ctx;
        }
        public Object lookup(String adapterName) {
            DataService dataSvc = txn.getContext().getService(DataService.class);
            return dataSvc.getEntityManager( adapterName );
        }

        public SchemaManager getSchemaManager() {
            DataService dataSvc = txn.getContext().getService(DataService.class);
            return dataSvc.getSchemaManager();
        }
        
    }
    
}
