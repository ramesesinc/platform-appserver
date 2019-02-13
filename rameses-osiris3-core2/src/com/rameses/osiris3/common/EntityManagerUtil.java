/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.common;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.data.DataService;
import com.rameses.osiris3.persistence.EntityManager;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.sql.SqlContext;
import groovy.lang.GroovyClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dell
 * used for rules
 */
public class EntityManagerUtil {
    private static Map<String, Class> metaClasses = Collections.synchronizedMap(new HashMap());

    public static EntityManager lookup(String schemaName) throws Exception {
        return lookup(schemaName, null);
    }

    public static EntityManager lookup(String schemaName, String sadapter) throws Exception {
        TransactionContext txn = TransactionContext.getCurrentContext();
        AbstractContext ac = txn.getContext();
        
        DataService dataSvc = txn.getContext().getService(DataService.class);
        
        String adapter = findAdapter(  dataSvc.getSchemaManager(), schemaName, sadapter);
        SqlContext sqc = dataSvc.getSqlContext(adapter);
        try {
            return getEntityManager(schemaName, dataSvc.getSchemaManager(), sqc, txn.getContext().getClassLoader());
        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static EntityManager getEntityManager(String schemaName, SchemaManager sm, SqlContext sqc, ClassLoader parentClassLoader) throws Exception {
        if(!metaClasses.containsKey(schemaName)) {
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
            Class metaClass = classLoader.parseClass( builder.toString() );
            metaClasses.put(schemaName, metaClass);
        }
        Class metaClass = (Class)metaClasses.get(schemaName);
        Class[] consts = new Class[]{SchemaManager.class, SqlContext.class};
        Object[] parms = new Object[]{sm, sqc};
        EntityManager em = (EntityManager)metaClass.getDeclaredConstructor(consts).newInstance(parms);
        em.setName(schemaName);
        return em;
    }
    
    private static String findAdapter( SchemaManager sm,  String schemaName, String adapter ) {
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
    
}
