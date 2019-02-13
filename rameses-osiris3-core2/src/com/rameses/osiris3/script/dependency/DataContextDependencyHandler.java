/*
 * PersistenceContextDependencyHandler.java
 *
 * Created on January 10, 2013, 1:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.dependency;


import com.rameses.annotations.DataContext;
import com.rameses.osiris3.data.DataService;
import com.rameses.osiris3.script.DependencyHandler;
import com.rameses.osiris3.script.ExecutionInfo;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.persistence.EntityManager;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.sql.SqlContext;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.lang.annotation.Annotation;

/**
 *
 * @author Elmo
 */
public class DataContextDependencyHandler extends DependencyHandler {
    
    private Class metaClass;
    
    public Class getAnnotation() {
        return DataContext.class;
    }
    
    private EntityManager getEntityManager(SchemaManager sm, SqlContext sqc, ClassLoader parentClassLoader) throws Exception {
        if(metaClass==null) {
            StringBuilder builder = new StringBuilder();
            builder.append( "public class ActiveEntity extends com.rameses.osiris3.persistence.EntityManager { \n" );
            builder.append( "    public ActiveEntity(com.rameses.osiris3.schema.SchemaManager m, com.rameses.osiris3.sql.SqlContext s, String n) { \n");
            builder.append( "       super(m,s,n);\n");
            builder.append( "    } \n");
            
            builder.append( "    public ActiveEntity(com.rameses.osiris3.schema.SchemaManager m, com.rameses.osiris3.sql.SqlContext s) { \n");
            builder.append( "       super(m,s);\n");
            builder.append( "    } \n");
            
            builder.append( "   public Object invokeMethod(String methodName, Object args) { \n");
            builder.append( "         return super.invokeSqlMethod( methodName, args ); \n");     
            builder.append( "   } \n");
            builder.append( "} \n");
            GroovyClassLoader classLoader = new GroovyClassLoader(parentClassLoader);
            metaClass = classLoader.parseClass( builder.toString() );
        }
        Class[] consts = new Class[]{SchemaManager.class, SqlContext.class};
        Object[] parms = new Object[]{sm, sqc};
        return (EntityManager)metaClass.getDeclaredConstructor(consts).newInstance(parms);
    }
    
    public class DynamicDataContext {
        private DataService dataSvc;
        private TransactionContext txnCtx;
        public DynamicDataContext( DataService svc, TransactionContext ctx ) {
            this.dataSvc = svc;
            this.txnCtx = ctx;
        }
        public Object lookup(String schemaName) throws Exception {
            return lookup(schemaName, null);
        }    
        public Object lookup(String schemaName, String adapter) throws Exception {
            if(schemaName==null) throw new Exception("Schema Name is required in DynamicDataContext.lookup");
            adapter = findAdapter( dataSvc.getSchemaManager(), schemaName, adapter );
            SqlContext sqc = dataSvc.getSqlContext(adapter);
            EntityManager em = getEntityManager(dataSvc.getSchemaManager(), sqc, txnCtx.getContext().getClassLoader());
            if(schemaName.trim().length()>0){
                em.setName(schemaName);
            }
            return em;
        }
    }

    private String findAdapter( SchemaManager sm,  String schemaName, String adapter ) {
        if(adapter == null) adapter = "";
        if(adapter.trim().length() == 0 && schemaName.trim().length() > 0 ) {
            SchemaElement elem = sm.getElement(schemaName);
            if(elem.getAdapter()!=null) adapter = elem.getAdapter();
            if(adapter.trim().length()==0 && elem.getSchema().getAdapter()!=null) {
                adapter = elem.getSchema().getAdapter();
            }
        }
        if(adapter.trim().length()==0) {
            adapter = "main";
        }
        return adapter;
    }
    
    public Object getResource(Annotation c, ExecutionInfo einfo) {
        DataContext adb = (DataContext)c;
        TransactionContext txn = TransactionContext.getCurrentContext();
        DataService dataSvc = txn.getContext().getService(DataService.class);
        
        if( adb.dynamic() == true ) {
            return new DynamicDataContext( dataSvc, txn );
        }
        
        String schemaName = adb.value();
        String adapter = findAdapter(  dataSvc.getSchemaManager(), schemaName,   adb.adapter());
        SqlContext sqc = dataSvc.getSqlContext(adapter);
        try {
            EntityManager em = getEntityManager(dataSvc.getSchemaManager(), sqc, txn.getContext().getClassLoader());
            if(schemaName.trim().length()>0){
                em.setName(schemaName);
            }
            return em;
        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    
    
}
