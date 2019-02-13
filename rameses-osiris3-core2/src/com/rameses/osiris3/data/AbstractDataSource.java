/*
 * AbstractDataSource.java
 *
 * Created on January 13, 2013, 8:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.data;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author Elmo
 */
public abstract class AbstractDataSource implements DataSource,  ConnectionWrapper.ConnectionListener {
    
    
    private String driverClass;
    private String url;
    private String user;
    private String pwd;
    private int minPoolSize = 5;
    private int maxPoolSize = 50;
    private String dialect;
    private Map extInfo;

    private int ver; 
    
    public void init(Map map) {
        dialect = (String)map.remove("dialect");
        driverClass = (String)map.remove("driverClass");
        url = (String)map.remove("url");
        user = (String)map.remove("user");
        pwd = (String)map.remove("pwd");
        if (map.containsKey("minPoolSize")) {
            minPoolSize = Integer.parseInt((String)map.remove("minPoolSize"));
        }
        if (map.containsKey("maxPoolSize")) {
            maxPoolSize = Integer.parseInt((String)map.remove("maxPoolSize"));
        }
        if (map.containsKey("ver")) {
            ver = Integer.parseInt(map.remove("ver").toString());
        }
        this.extInfo = map;
    }
    
    protected abstract Connection createConnection(String username, String pwd) throws SQLException;    
    public abstract void destroy();
    
    
    public final Connection getConnection() throws SQLException {
        Connection c = createConnection(getUser(), getPwd());
        return new ConnectionWrapper(c,this);
    }
    
    public final Connection getConnection(String username, String password) throws SQLException {
        Connection c = createConnection(username, password);
        return new ConnectionWrapper(c,this);
    }
    
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }
    
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }
    
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }
    
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }
    
    
    public <T extends Object> T unwrap(Class<T> T) throws SQLException {
        throw new SQLException("unwrap not supported");
    }
    
    public boolean isWrapperFor(Class<?> T) throws SQLException {
        throw new SQLException("isWrapperFor not supported");
    }
    
    public void onConnectionClose(Connection conn) {
        //do nothing...
    }
    
    /*********************
     * extended info.
     *********************/
    public String getDriverClass() {
        return driverClass;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getUser() {
        return user;
    }
    
    public String getPwd() {
        return pwd;
    }
    
    public int getMinPoolSize() {
        return minPoolSize;
    }
    
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    public String getDialect() {
        return dialect;
    }
    
    public int getVer() {
        return ver; 
    }
    
    public String getProperty( String name ) {
        Object value = (extInfo == null ? null: extInfo.get(name)); 
        return (value == null? null: value.toString()); 
    }
    
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
}
