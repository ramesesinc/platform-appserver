/*
 * TestFire.java
 * JUnit based test
 *
 * Created on January 10, 2013, 2:15 PM
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
public class TestFire extends TestCase {
    
    public TestFire(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testScriptService() throws Exception {
        Map conf = new HashMap();
        conf.put("app.context", "osiris3");
        conf.put("app.host", "localhost:8070" );
        conf.put("readTimeout", "30000" );
        ScriptServiceContext svc = new ScriptServiceContext(conf);
        Map env = new HashMap();
        env.put("sessionid", "ELMSKI" );
        MyTest m = svc.create("MyFirstService", env, MyTest.class);
        System.out.println("result is "+m.sayHello("elmo"));
        //System.out.println("result is->"+m.testCache("firstname"));
    }
    
    /*
    public void testEJB() throws Exception {
        Map conf = new HashMap();
        conf.put("app.context", "osiris3");
        conf.put("app.host", "localhost:8070" );
        EJBServiceContext svc = new EJBServiceContext(conf);
        Map env = new HashMap();
        env.put("sessionid", "ELMSKI" );
        MyTest m = svc.create("TestService", MyTest.class);
        m.sayHello("elmo");
    }
     */

    private interface MyTest {
        Object sayHello(String name);
        void resume();
        String testCache(String name);
    }
    
    public void xxtestResumeService() throws Exception {
        Map conf = new HashMap();
        conf.put("app.context", "osiris3");
        conf.put("app.host", "localhost:8070" );
        conf.put("readTimeout", "30000" );
        ScriptServiceContext svc = new ScriptServiceContext(conf);
        Map env = new HashMap();
        env.put("sessionid", "ELMSKI" );
        MyTest m = svc.create("tasks/Task1Service", env, MyTest.class);
        m.resume();
        
    }
    
}
