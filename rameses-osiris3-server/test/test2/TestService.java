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
public class TestService extends TestCase {
    
    private ScriptServiceContext svc;
    
    public TestService(String testName) {
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
        Map sayTest(Map data);
        String getStringInterface();
        void sendMessage( Object data );
    }
    
    public void testFire1() throws Exception {
        TestIntf test = svc.create("MyFirstService", new HashMap(), TestIntf.class);
        Map m = new HashMap();
        m.put("objid","EMN3");
        m.put("name","ELMOKIXss");
        m.put("firstnam1e","DANNY");
        m.put("lastname","BONNY");
        System.out.println( test.sayTest(m).toString() );
    }
    
    public void xtestSendMessage() throws Exception {
        TestIntf test = svc.create("MyFirstService", new HashMap(), TestIntf.class);
        Map m = new HashMap();
        m.put("objid","EMN3");
        m.put("name","ELMOKIXss");
        m.put("firstnam1e","DANNY");
        m.put("lastname","BONNY");
        test.sendMessage( m );
    }
    
}
