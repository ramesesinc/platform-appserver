/*
 * PersistenceContextDependencyHandler.java
 *
 * Created on January 10, 2013, 1:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.dependency;

import com.rameses.annotations.ActiveDB;
import com.rameses.osiris3.activedb.ActiveDBTransactionManager;
import com.rameses.osiris3.data.DataService;
import com.rameses.osiris3.script.DependencyHandler;
import com.rameses.osiris3.script.ExecutionInfo;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.persistence.EntityManager;

import java.lang.annotation.Annotation;

/**
 *
 * @author Elmo
 */
public class ActiveDBDependencyHandler extends DependencyHandler {
    
    public Class getAnnotation() {
        return ActiveDB.class;
    }
    
    public Object getResource(Annotation c, ExecutionInfo einfo) {
        ActiveDB adb = (ActiveDB)c;
        TransactionContext txn = TransactionContext.getCurrentContext();
        if(!adb.dynamic()) {
            DataService dataSvc = txn.getContext().getService(DataService.class);
            String adapter = adb.adapter();
            if( adapter.trim().length() == 0 ) adapter = adb.em();
            if( adapter.trim().length() == 0 ) adapter = "main";
            EntityManager em = dataSvc.getEntityManager( adapter );
            ActiveDBTransactionManager dbm = txn.getManager( ActiveDBTransactionManager.class );
            return dbm.create( adb.value(), em );
        }
        else {
            return new DynamicActiveDB(txn, adb.value());
        }
    }
    
    public static class DynamicActiveDB {
        private TransactionContext txn;
        private String defaultSchemaName;
        public DynamicActiveDB( TransactionContext ctx, String value) {
            this.txn = ctx;
            this.defaultSchemaName = value;
        }
        public Object lookup(String adapterName, String schemaName) throws Exception{
            DataService dataSvc = txn.getContext().getService(DataService.class);
            EntityManager em = dataSvc.getEntityManager( adapterName );
            ActiveDBTransactionManager dbm = txn.getManager( ActiveDBTransactionManager.class );
            return dbm.create( schemaName, em );
        }
        public Object lookup(String adapterName) throws Exception{
            if( defaultSchemaName == null )
                throw new Exception("Please indicate a value in ActiveDB dynamic");
            return lookup( adapterName, defaultSchemaName );
        }
    }
    
    
}
