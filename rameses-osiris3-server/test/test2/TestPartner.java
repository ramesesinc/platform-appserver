/*
 * TestServer.java
 * JUnit based test
 *
 * Created on January 8, 2013, 3:41 PM
 */

package test2;

import com.rameses.service.ScriptServiceContext;

import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class TestPartner extends TestCase {
    
    public TestPartner(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        
    }
    
    protected void tearDown() throws Exception {
    }
    
    public void testSearch() throws Exception {
        Map conf = new HashMap();
        conf.put("app.context", "osiris3");
        conf.put("app.host", "localhost:8070" );
        conf.put("readTimeout", "30000" );
        ScriptServiceContext svc = new ScriptServiceContext(conf);
        Map env = new HashMap();
        env.put("sessionid", "ELMSKI" );
        LookupTest m = svc.create("SearchPartnerService", env, LookupTest.class);
        System.out.println("search result is " + m.search() );
    }
    
    private interface LookupTest {
        String search();
        
    }
    
}
