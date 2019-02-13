/*
 * TomcatCpDSProvider.java
 *
 * Created on September 13, 2013, 3:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tomcat.cp;

import com.rameses.osiris3.data.AbstractDataSource;
import com.rameses.osiris3.data.DsProvider;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 *
 * @author Elmo
 */
public class TomcatCpDsProvider implements DsProvider 
{
    public AbstractDataSource createDataSource(String name, Map map) {
        try {
            return new TomcatCPDataSource(name,map);
        } catch(Throwable t) {
            t.printStackTrace();
            return null; 
        } finally { 
            
        }
    }
    
    public class TomcatCPDataSource extends AbstractDataSource 
    {
        private String name;
        private DataSource datasource;
        
        public TomcatCPDataSource(String name, Map map) {
            this.name = name; 
            init(map);
        }
        
        public void init(Map map) {
            super.init(map);
            
            try {
                PoolProperties p = new PoolProperties();
                p.setUrl(getUrl());
                p.setDriverClassName(getDriverClass());
                p.setUsername(getUser());
                p.setPassword(getPwd());
                
                p.setJmxEnabled(true); 
                p.setTestOnBorrow(true); 
                
                String validationQuery = null; 
                if (map.containsKey("validationQuery")) {
                    Object ov = map.get("validationQuery");
                    validationQuery = (ov == null? null: ov.toString()); 
                } 
                if (validationQuery == null || validationQuery.trim().length() == 0) {
                    validationQuery = "SELECT 1"; 
                }
                p.setValidationQuery(validationQuery);
                
                int validationInterval = 30000;
                if (map.containsKey("validationInterval")) {
                    Object ov = map.get("validationInterval");
                    if (ov != null) {
                        validationInterval = Integer.parseInt(ov.toString());
                    } 
                } 
                p.setValidationInterval(validationInterval); 
                p.setTimeBetweenEvictionRunsMillis(validationInterval); 

                if (map.containsKey("timeBetweenEvictionRunsMillis")) {
                    Object ov = map.get("timeBetweenEvictionRunsMillis");
                    if (ov != null) {
                        int timeBetweenEvictionRunsMillis = Integer.parseInt(ov.toString());
                        p.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
                    } 
                } 
                
                if (map.containsKey("minEvictableIdleTimeMillis")) {
                    Object ov = map.get("minEvictableIdleTimeMillis");
                    if (ov != null) {
                        int minEvictableIdleTimeMillis = Integer.parseInt(ov.toString());
                        p.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis); 
                    } 
                } 
                
                int maxActive = 100;
                if (map.containsKey("maxActive")) {
                    Object ov = map.get("maxActive");
                    if (ov != null) maxActive = Integer.parseInt(ov.toString()); 
                } 
                p.setMaxActive(maxActive);    

                int initialSize = 5;
                if (map.containsKey("initialSize")) {
                    Object ov = map.get("initialSize");
                    if (ov != null) initialSize = Integer.parseInt(ov.toString()); 
                } 
                p.setInitialSize(initialSize); 
                
                int maxWait = 10;
                if (map.containsKey("maxWait")) {
                    Object ov = map.get("maxWait");
                    if (ov != null) {
                        maxWait = Integer.parseInt(ov.toString());
                        p.setMaxWait(maxWait); 
                    } 
                } 
                   
                int maxIdle = 10;
                if (map.containsKey("maxIdle")) {
                    Object ov = map.get("maxIdle");
                    if (ov != null) {
                        maxIdle = Integer.parseInt(ov.toString());
                        p.setMaxIdle(maxIdle); 
                    } 
                }                                 
                
                int minIdle = initialSize;
                if (map.containsKey("minIdle")) {
                    Object ov = map.get("minIdle");
                    if (ov != null) minIdle = Integer.parseInt(ov.toString()); 
                }                 
                p.setMinIdle(minIdle); 
                                
                int maxAge = -1; 
                if (map.containsKey("maxAge")) {
                    Object ov = map.get("maxAge");
                    if (ov != null) {
                        maxAge = Integer.parseInt(ov.toString());
                    } 
                } 
                p.setMaxAge(maxAge);                
                                
                //database connection pool leak settings
                p.setRemoveAbandoned(true);
                p.setLogAbandoned(true);
                p.setAbandonWhenPercentageFull(50);

                if (map.containsKey("removeAbandonedTimeout")) {
                    Object ov = map.get("removeAbandonedTimeout");
                    if (ov != null) {
                        int removeAbandonedTimeout = Integer.parseInt(ov.toString());
                        p.setRemoveAbandonedTimeout(removeAbandonedTimeout); 
                    } 
                }                 
                
                String isolationLevel = (""+map.get("isolationLevel")).replaceAll("[\\s]{1,}","").toUpperCase();
                if ("READ_UNCOMMITTED".equals( isolationLevel )) {
                    p.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED); 
                } else if ("READ_COMMITTED".equals( isolationLevel )) {
                    p.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); 
                } else if ("REPEATABLE_READ".equals( isolationLevel )) {
                    p.setDefaultTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ); 
                } else if ("SERIALIZABLE".equals( isolationLevel )) {
                    p.setDefaultTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); 
                }
                
                //
                //
                p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                        + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
                datasource = new DataSource();
                datasource.setPoolProperties(p); 
                System.out.println("#######################################");
                System.out.println("# TomcatCPDataSource datasource config:");
                System.out.println("#######################################");
                System.out.println(" driverClassName="+p.getDriverClassName());
                System.out.println(" poolName="+p.getPoolName());
                System.out.println(" defaultCatalog="+p.getDefaultCatalog());
                System.out.println(" initSQL="+p.getInitSQL());
                System.out.println(" abandonWhenPercentageFull="+p.getAbandonWhenPercentageFull());
                System.out.println(" defaultTransactionIsolation="+p.getDefaultTransactionIsolation());
                System.out.println(" initialSize="+p.getInitialSize());
                System.out.println(" maxActive="+p.getMaxActive());
                System.out.println(" minIdle="+p.getMinIdle());
                System.out.println(" maxIdle="+p.getMaxIdle());
                System.out.println(" maxWait="+p.getMaxWait());
                System.out.println(" maxAge="+p.getMaxAge());
                System.out.println(" minEvictableIdleTimeMillis="+p.getMinEvictableIdleTimeMillis());                
                System.out.println(" removeAbandonedTimeout="+p.getRemoveAbandonedTimeout());
                System.out.println(" suspectTimeout="+p.getSuspectTimeout());
                System.out.println(" testOnBorrow="+p.isTestOnBorrow());
                System.out.println(" testOnConnect="+p.isTestOnConnect());
                System.out.println(" testOnReturn="+p.isTestOnReturn());
                System.out.println(" testWhileIdle="+p.isTestWhileIdle());
                System.out.println(" timeBetweenEvictionRunsMillis="+p.getTimeBetweenEvictionRunsMillis());
                System.out.println(" useDisposableConnectionFacade="+p.getUseDisposableConnectionFacade());
                System.out.println(" useLock="+p.getUseLock());
                System.out.println(" useEquals="+p.isUseEquals());
                System.out.println(" validationQuery="+p.getValidationQuery());
                System.out.println(" validationInterval="+p.getValidationInterval());
                System.out.println(" ");
            } catch(RuntimeException re) {
                throw re;
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            } 
        }
        
        protected Connection createConnection(String username, String pwd) throws SQLException { 
            boolean failed = true; 
            try { 
                Connection conn = datasource.getConnection(); 
                failed = false; 
                return conn; 
            } finally {
                if ( failed ) {
                    System.out.println("["+ this.name +"] failed to create connection from datasource");
                }
            }
        }
        
        public void destroy() {
            datasource.close();
        }
        
//        private void dump(PoolProperties p) {
//            System.out.println("#######################################");
//            System.out.println("# dump datasource config:             ");
//            System.out.println("#######################################");
//            System.out.println(" TestOnBorrow="+p.isTestOnBorrow());
//            System.out.println(" TestWhileIdle="+p.isTestWhileIdle());
//            System.out.println(" TestOnReturn="+p.isTestOnReturn());
//            System.out.println(" TestOnConnect="+p.isTestOnConnect());
//            System.out.println(" validationQuery="+p.getValidationQuery());
//            System.out.println(" validationInterval="+p.getValidationInterval());
//            System.out.println(" timeBetweenEvictionRunsMillis="+p.getTimeBetweenEvictionRunsMillis());
//            System.out.println(" minEvictableIdleTimeMillis="+p.getMinEvictableIdleTimeMillis());                
//            System.out.println(" initialSize="+p.getInitialSize());
//            System.out.println(" minIdle="+p.getMinIdle());
//            System.out.println(" maxActive="+p.getMaxActive());
//            System.out.println(" maxIdle="+p.getMaxIdle());
//            System.out.println(" maxWait="+p.getMaxWait());
//            System.out.println(" maxAge="+p.getMaxAge());
//            System.out.println(" removeAbandoned="+p.isRemoveAbandoned());
//            System.out.println(" LogAbandoned="+p.isLogAbandoned());
//            System.out.println(" removeAbandonedTimeout="+p.getRemoveAbandonedTimeout());
//            System.out.println(" ");
//        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
