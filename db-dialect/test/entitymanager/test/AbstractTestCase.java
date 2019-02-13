/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitymanager.test;

import com.rameses.osiris3.data.MockConnectionManager;
import com.rameses.osiris3.persistence.EntityManager;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.sql.SimpleDataSource;
import com.rameses.osiris3.sql.SqlContext;
import com.rameses.osiris3.sql.SqlManager;
import com.rameses.sql.dialect.MsSqlDialect;
import com.rameses.sql.dialect.MySqlDialect;
import java.util.List;
import junit.framework.TestCase;

/**
 * @author dell.
 */
public abstract class AbstractTestCase extends TestCase {

    protected SqlManager sqlManager;
    protected SchemaManager schemaManager;
    protected MockConnectionManager cm;
    protected EntityManager em;
    
    public AbstractTestCase() {
        super("Test");
    }

    @Override
    protected void setUp() throws Exception {
        sqlManager = SqlManager.getInstance();
        schemaManager = SchemaManager.getInstance();
        em = new EntityManager(schemaManager, createContext(), getEntityname());
        em.setDebug(true);
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public String getEntityname() {
        return  "entityindividual";
    }
    
    public abstract String getDialect();
    
    public String getDbname() {
        return "testdb";
    }
    
    private SqlContext createContext() throws Exception {
        cm = new MockConnectionManager();
        SimpleDataSource ds = null;
        SqlContext sqlc = null;
        if( getDialect().equals("mysql")) {
            ds = new SimpleDataSource("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/"+getDbname(), "root", "1234");
            sqlc = sqlManager.createContext(cm.getConnection("main", ds));
            sqlc.setDialect(new MySqlDialect());
        }
        else {
            //SQL SERVER
            ds = new SimpleDataSource("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://127.0.0.1;DatabaseName="+getDbname(), "sa", "1234");
            sqlc = sqlManager.createContext(cm.getConnection("main", ds));
            sqlc.setDialect(new MsSqlDialect());
        }

        return sqlc;

    }
    
    public static interface ExecHandler {
        void execute() throws Exception;
    }
    
    public void exec( ExecHandler h  ) throws Exception {
        try {
            h.execute();
            cm.commit();
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            cm.close();
        }
    }
    
    public void printList(List list) {
        for(Object obj: list) {
            System.out.println(obj);
        }
    }
    
}
