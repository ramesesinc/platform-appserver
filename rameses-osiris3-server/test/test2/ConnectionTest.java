/*
 * TestServer.java
 * JUnit based test
 *
 * Created on January 8, 2013, 3:41 PM
 */

package test2;

import com.rameses.common.AsyncHandler;
import com.rameses.service.ScriptServiceContext;

import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class ConnectionTest extends TestCase {
    
    private ScriptServiceContext svc;
    
    public ConnectionTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        Map conf = new HashMap();
        conf.put("app.cluster", "osiris3");
        conf.put("app.context", "app1");
        conf.put("app.host", "localhost:8070" );
        conf.put("readTimeout", "30000" );
        svc = new ScriptServiceContext(conf);
    }
    
    protected void tearDown() throws Exception {
    }
    
    private interface TestIntf {
        Object sendMessage( Object data );
        Object sendMessage( Object data, AsyncHandler handler );
        Object sendMessage2( Object data );
    }
    
    public void testSendMessage() throws Exception {
        TestIntf test = svc.create("ConnectionTest", new HashMap(), TestIntf.class);
        Map m = new HashMap();
        m.put("objid","EMN4");
        test.sendMessage( m );
    }
    
     
}
