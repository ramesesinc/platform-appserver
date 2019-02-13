/*
 * PersistenceContextDependencyHandler.java
 *
 * Created on January 10, 2013, 1:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.dependency;

import com.rameses.annotations.SqlContext;
import com.rameses.osiris3.data.DataService;
import com.rameses.osiris3.script.DependencyHandler;
import com.rameses.osiris3.script.ExecutionInfo;
import com.rameses.osiris3.core.TransactionContext;


import java.lang.annotation.Annotation;

/**
 *
 * @author Elmo
 */
public class SqlContextDependencyHandler extends DependencyHandler {
    
    public Class getAnnotation() {
        return SqlContext.class;
    }

    public Object getResource(Annotation c, ExecutionInfo einfo) {
        SqlContext p = (SqlContext)c;
        TransactionContext txn = TransactionContext.getCurrentContext();
        if(! p.dynamic()) {
            DataService dataSvc = txn.getContext().getService(DataService.class);
            return dataSvc.getSqlContext( p.value() );
        }
        else {
            return new DynamicSqlContext( txn );
        }
    }

     public static class DynamicSqlContext {
        private TransactionContext txn;
        public DynamicSqlContext( TransactionContext ctx ) {
            this.txn = ctx;
        }
        public Object lookup(String adapterName) {
            DataService dataSvc = txn.getContext().getService(DataService.class);
            return dataSvc.getSqlContext( adapterName );
        }
    }
    
}
