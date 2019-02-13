/*
 * ConnectionTransactionManager.java
 *
 * Created on January 30, 2013, 4:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.data;

import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.core.TransactionManager;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author Elmo
 */
public class ConnectionTransactionManager implements TransactionManager {
    
    private OsirisServer server;
    private Map<String, Connection> connections = Collections.synchronizedMap(new HashMap());
    
    public ConnectionTransactionManager(OsirisServer s) {
        this.server = s;
    }
    
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
                if(c instanceof ConnectionWrapper)
                    ((ConnectionWrapper)c).getConnection().setAutoCommit(false);
                else
                    c.setAutoCommit( false );
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
