/*
 * DataService.java
 *
 * Created on January 28, 2013, 1:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.data;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.ContextService;
import com.rameses.osiris3.core.SharedContext;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.persistence.EntityManager;
import com.rameses.osiris3.schema.SchemaConf;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.schema.SchemaResourceProvider;
import com.rameses.osiris3.sql.AbstractSqlDialect;
import com.rameses.osiris3.sql.SqlConf;
import com.rameses.osiris3.sql.SqlContext;
import com.rameses.osiris3.sql.SqlDialect;
import com.rameses.osiris3.sql.SqlManager;
import com.rameses.osiris3.sql.SqlUnitResourceProvider;
import com.rameses.util.Service;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class DataService extends ContextService {
    
    protected SchemaManagerImpl schemaManager;
    protected SqlManager sqlManager;
    
    private Map<String, SqlDialect> dialects;
    
    
    public Class getProviderClass() {
        return DataService.class;
    }
    
    public final int getRunLevel() {
        return 0;
    }
    
    public void start() throws Exception {
        //sql context
        SqlConf sqlc = new SqlConf();
        sqlc.setResourceProvider(getSqlResourceProvider());
        sqlManager = new SqlManager(sqlc);
        //schema manager
        schemaManager = new SchemaManagerImpl();
        SchemaConf c = new SchemaConf(schemaManager);
        c.setResourceProvider( getSchemaResourceProvider() );
        schemaManager.setConf( c );
        
        dialects =  Collections.synchronizedMap( new HashMap() );
        Iterator<SqlDialect> iter = Service.providers( SqlDialect.class, server.getClass().getClassLoader() );
        while(iter.hasNext()) {
            SqlDialect dia = iter.next();
            dialects.put( dia.getName(), dia );
        }
    }
    
    public void stop() throws Exception {
    }
    
    protected SqlUnitResourceProvider getSqlResourceProvider() {
        return new CustomSqlResourceProvider();
    }
    
    protected SchemaResourceProvider getSchemaResourceProvider() {
        return  new CustomSchemaResourceProvider();
    }
    
    public final SqlContext getSqlContext(String name) {
        if(name.startsWith("java:")) name = name.substring(5);
        DataAdapter da = context.getResource( DataAdapter.class, name );
        TransactionContext txn = TransactionContext.getCurrentContext();
        if ( txn == null ) throw new RuntimeException("There is no current transaction context active");
        
        ConnectionTransactionManager tc = txn.getManager( ConnectionTransactionManager.class );
        DsServerResource dsr = server.getResource( DsServerResource.class );
        AbstractDataSource ds = (AbstractDataSource) dsr.getDataSource( da.getDsName() );
        
        Connection conn = tc.getConnection(  da.getDsName(), ds );
        SqlContext sqc = sqlManager.createContext( conn );
        sqc.setCatalog( da.getCatalog() );
        
        SqlDialect d = dialects.get( ds.getDialect());
        if ( d instanceof AbstractSqlDialect ) {
            ((AbstractSqlDialect) d).setVersion( ds.getVer()); 
        }
        sqc.setDialect( d );
        return sqc;
    }
    
    public final EntityManager getEntityManager(String name) {
        if(name.startsWith("java:")) name = name.substring(5);
        
        DataAdapter da = context.getResource( DataAdapter.class, name );
        TransactionContext txn = TransactionContext.getCurrentContext();
        if ( txn == null ) throw new RuntimeException("There is no current transaction context active");
        
        ConnectionTransactionManager tc = txn.getManager( ConnectionTransactionManager.class );
        DsServerResource dsr = server.getResource( DsServerResource.class );
        AbstractDataSource ds = (AbstractDataSource) dsr.getDataSource( da.getDsName() );
        
        Connection conn = tc.getConnection(  da.getDsName(), ds );
        SqlContext sqc = sqlManager.createContext( conn );
        sqc.setCatalog( da.getCatalog() );
        
        SqlDialect d = dialects.get( ds.getDialect() );
        if ( d instanceof AbstractSqlDialect ) {
            ((AbstractSqlDialect) d).setVersion( ds.getVer()); 
        }        
        sqc.setDialect( d );         
        return new EntityManager(schemaManager, sqc);
    }
    
    private class CustomSqlResourceProvider implements SqlUnitResourceProvider {
        public InputStream getResource(String name) {
            final String path = "sql/"+name;
            if( context instanceof AppContext ) {
                AppContext ac = (AppContext)context;
                SharedContext sc = ac.getSharedContext();
                if(sc!=null) {
                    InputStream is = sc.getClassLoader().getResourceAsStream(  path  );
                    if(is!=null) return is;
                }
                return ac.getClassLoader().getResourceAsStream(path);
            } else {
                return context.getClassLoader().getResourceAsStream(path);
            }
        }
    }
    
    private class CustomSchemaResourceProvider implements SchemaResourceProvider {
        public InputStream getResource(String name) {
            final String path = "schema/"+name+".xml";
            if( context instanceof AppContext ) {
                AppContext ac = (AppContext)context;
                SharedContext sc = ac.getSharedContext();
                if(sc!=null) {
                    InputStream is = sc.getClassLoader().getResourceAsStream(  path  );
                    if(is!=null) return is;
                }
                return ac.getClassLoader().getResourceAsStream(path);
            } else {
                return context.getClassLoader().getResourceAsStream(path);
            }
        }
    }
    
    public void clearSchema(String name) {
        if(name==null)
            this.schemaManager.getCache().clear();
        else
            this.schemaManager.getCache().remove(name);
    }
    
    public void clearSql(String name) {
        if(name==null)
            this.sqlManager.getCache().clear();
        else
            this.sqlManager.getCache().remove(name);
    }
    
    public SchemaManager getSchemaManager() {
        return this.schemaManager;
    }
    
}
