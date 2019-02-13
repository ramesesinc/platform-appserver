/*
 * SimpleAbstractDsProvider.java
 *
 * Created on January 30, 2013, 11:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Elmo
 */
public class SimpleAbstractDsProvider implements DsProvider  {

    public AbstractDataSource createDataSource(String name, Map map) {
         return new SimpleAbstractDataSource(name, map);
    }
    
    public class SimpleAbstractDataSource extends AbstractDataSource  {
        
        public SimpleAbstractDataSource(String name, Map map) {
            init(map);
        }
        
        public void init(Map map) {
            super.init(map);
            try {
                Class.forName(getDriverClass());
            } catch(Exception e) {
                throw new RuntimeException("Driverclass " + getDriverClass() + " not registered." + e.getMessage());
            }
        }
        public Connection createConnection(String username, String pwd) throws SQLException {
            return DriverManager.getConnection( getUrl(),username,pwd );
        }
        public void destroy() {
            System.out.println("destroying simple datasource");
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
}
