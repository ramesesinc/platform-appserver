/*
 * TransactionTest.java
 * JUnit based test
 *
 * Created on February 19, 2013, 9:57 AM
 */

package etracsorg;

import com.rameses.service.ScriptServiceContext;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class TransactionTest extends TestCase {
    
    private ScriptServiceContext svc;
    
    public TransactionTest(String testName) {
        super(testName);
    }
    
    protected void tearDown() throws Exception {
    }
    
    protected void setUp() throws Exception {
        Map conf = new HashMap();
        conf.put("app.cluster", "osiris3");
        conf.put("app.context", "test");
        conf.put("app.host", "etracs.org" );
        conf.put("readTimeout", "30000" );
        svc = new ScriptServiceContext(conf);
    }
    
    private interface TestIntf {
        Object send( Object data );
    }
    

    public void testSendMessage2() throws Exception {
        TestIntf test = svc.create("TestService", new HashMap(), TestIntf.class);
        Map m = new HashMap();
        m.put("channel","test");
        m.put("msg","Hello everybody");
        System.out.println("result is ->"+test.send( m ));
    }
    
}
