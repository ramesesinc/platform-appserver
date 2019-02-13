/*
 * TestServer.java
 * JUnit based test
 *
 * Created on January 8, 2013, 3:41 PM
 */

package test2;

import com.rameses.common.AsyncHandler;
import com.rameses.common.AsyncResponse;
import com.rameses.service.ScriptServiceContext;

import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class SimpleTest extends TestCase {
    
    private ScriptServiceContext svc;
    
    public SimpleTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        Map conf = new HashMap();
        conf.put("app.context", "osiris3");
        conf.put("app.host", "localhost:8070" );
        conf.put("readTimeout", "30000" );
        svc = new ScriptServiceContext(conf);    
    }
    
    protected void tearDown() throws Exception {
    }
    
    private interface TestIntf {
        String sayHello(String name);
        String sayHello(String name, AsyncHandler h);
    }

    class MyHandler implements AsyncHandler {
        @Override
        public void onError(Exception e) {
        }

        @Override
        public void onMessage(Object o) {
            System.out.println("received message " + o);
        }

        @Override
        public void call(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    
    public void xxtestSimple() throws Exception {
        Map env = new HashMap();
        env.put("sessionid", "ELMSKI" );
        TestIntf m = svc.create("SimpleService", env, TestIntf.class);
        System.out.println("search result is " + m.sayHello("elmox") );
    }
    
    public void testAsync() throws Exception {
        Map env = new HashMap();
        env.put("sessionid", "ELMSKI" );
        TestIntf m = svc.create("SimpleService", env, TestIntf.class);
        m.sayHello("elmox", new MyHandler() );
        Thread.sleep( 3000 );
    }
    
}
