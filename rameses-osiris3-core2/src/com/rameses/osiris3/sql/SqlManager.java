/*
 * SqlUnitFactory.java
 *
 * Created on August 13, 2010, 2:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.sql;

import java.sql.Connection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import javax.sql.DataSource;

/**
 * for caching Sql Units, this class must be extended.
 *
 */
public class SqlManager {
    
    //=== static area ===
    private static SqlManager instance;
    
    public static void setInstance(SqlManager sm) {
        instance = sm;
    }
    
    public static SqlManager getInstance() {
        if(instance==null) {
            instance = new SqlManager(new SqlConf());
        }
        return instance;
    }
    
    
    //=== instance area ===
    private SqlConf conf = new SqlConf();
    
    public SqlConf getConf() {
        return conf;
    }
    
    public boolean isCached() {
        boolean _cached = true;
        try {
            String t = System.getProperty("cached_resource");
            if(t!=null)_cached = Boolean.parseBoolean(""+t);
        }
        catch(Exception ign){;}
        return _cached;
    } 
    
    private Map<String,SqlUnit> cache = Collections.synchronizedMap(new Hashtable());
    
    public SqlManager(SqlConf conf) {
        this.conf = conf;
        this.conf.load();
    }
    
    public SqlUnit getParsedSqlUnit(String statement) {
        String key = statement.hashCode()+"";
        SqlUnit su = cache.get(key);
        if(su==null) {
            su = new SqlUnit(statement);
            if(isCached()) cache.put(key, su);
        }
        return su;
    }
    
    public SqlUnit getSqlUnit(String key, SqlUnitSource src) {
        SqlUnit su = cache.get(key);
        if(su==null) {
            su = src.getStatement();
            if(isCached()) cache.put(key, su);
        }
        return su;
    }
    
    public SqlUnit getNamedSqlUnit(String name, SqlDialect dialect) {
        int extIndex = name.lastIndexOf(".");
        String unitName = name;
        //type is represnted in the extension part.
        String type = name.substring( extIndex+1 );
        
        SqlUnit su = cache.get(unitName);
        if( su!=null) return su;
        
        Map<String, SqlUnitProvider> providers = getConf().getSqlUnitProviders();
        //extension represents the type of Sql unit.
        if(!providers.containsKey(type)) {
            throw new RuntimeException("Sql unit factory error. There is no Sql Unit provider for type " + type );
        }
        
        su = providers.get(type).getSqlUnit(unitName, dialect );
        if(su==null) {
            throw new RuntimeException("Sql unit " + name + " is not found");
        }
        
        if(isCached()) cache.put(name, su);
        
        return su;
    }
    
    public void destroy() {
        getConf().destroy();
    }
    
    
    public SqlContext createContext() {
        SqlContext sm = new SqlContext();
        sm.setSqlManager(this);
        return sm;
    }
    
    public SqlContext createContext(Connection c) {
        try {
            SqlContext sm = new SqlContext();
            sm.setConnection( c );
            sm.setSqlManager(this);
            return sm;
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public SqlContext createContext(DataSource ds) {
        try {
            SqlContext sm = new SqlContext();
            sm.setConnection( ds.getConnection() );
            sm.setSqlManager(this);
            return sm;
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public Map<String, SqlUnit> getCache() {
        return cache;
    }
    
}
