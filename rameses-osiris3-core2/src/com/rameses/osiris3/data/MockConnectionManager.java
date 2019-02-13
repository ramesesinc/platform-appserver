/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.data;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author dell
 * This is primarily used for testing
 */
public class MockConnectionManager {
    
    public static MockConnectionManager instance = new MockConnectionManager();
    
    public static MockConnectionManager getInstance() {
        return instance;
    }
    
    private Map<String, Connection> connections = Collections.synchronizedMap(new HashMap());
    
    public void rollback() {
        for(Connection c: connections.values()) {
            try {
                if(c instanceof ConnectionWrapper)
                    c = ((ConnectionWrapper)c).getConnection();
                if(c.getAutoCommit() == false) {
                    c.rollback();
                }
                c.setAutoCommit(true);
            } catch(Exception ign){;}
        }
    }
    
    public void commit() {
        for(Connection c: connections.values()) {
            try {
                if(c instanceof ConnectionWrapper)
                    c = ((ConnectionWrapper)c).getConnection();
                if(c.getAutoCommit() == false) {
                    c.commit();
                }
                c.setAutoCommit(true);
            } catch(Exception ign){;}
        }
    }
    
    /***
     * we need two parameters to get the datasource because dsname is shared
     * in the global server. Thus, we add the adapter name first then dsname
     * to ensure its a different connection
     */
    public Connection getConnection(String dsname, DataSource ds)  {
        if( ! connections.containsKey(dsname) ) {
            //get datasource from pool
            try {
                Connection c = ds.getConnection();
                if(c instanceof ConnectionWrapper) {
                    ((ConnectionWrapper)c).getConnection().setAutoCommit(false);
                }    
                else {
                    c.setAutoCommit( false );
                    c = new ConnectionWrapper(c, null);
                }    
                connections.put(dsname, c);
            } catch(RuntimeException re) {
                throw re; 
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return connections.get(dsname);
    }
    
    public void close() {
        for(Connection c: connections.values()) {
            try {
                if(c instanceof ConnectionWrapper )
                    c = ((ConnectionWrapper)c).getConnection();
                c.close();
            } catch(Exception ign){;}
        }
        connections.clear();
    }
    
}
