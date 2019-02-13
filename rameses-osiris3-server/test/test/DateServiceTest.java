/*
 * TestServer.java
 * JUnit based test
 *
 * Created on January 8, 2013, 3:41 PM
 */

package test;

import com.rameses.service.ScriptServiceContext;

import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class DateServiceTest extends TestCase {
    
    private ScriptServiceContext svc;
    
    public DateServiceTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        Map conf = new HashMap();
        conf.put("app.cluster", "osiris3"); 
        conf.put("app.context", "clfc");
        conf.put("app.host", "localhost:8070" );
        conf.put("readTimeout", "30000" );
        svc = new ScriptServiceContext(conf);    
    }
    
    protected void tearDown() throws Exception {
    }
    
    private interface TestIntf {
        Object getServerDate() throws Exception;
    }

    public void testMain() throws Exception {
        Map env = new HashMap();
        env.put("sessionid", "TEST-SESSION");
        
        TestIntf m = svc.create("DateService", env, TestIntf.class);
        System.out.println("serverDate is " + m.getServerDate()); 
    }
    
}
