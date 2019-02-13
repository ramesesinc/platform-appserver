/*
 * TestServer.java
 * JUnit based test
 *
 * Created on January 8, 2013, 3:41 PM
 */

package test2;

import com.rameses.service.ScriptServiceContext;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class RuleServiceTest extends TestCase {
    
    private ScriptServiceContext svc;
    
    public RuleServiceTest(String testName) {
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
    
    private interface RuleIntf {
        Object execute( Object data );
    }
    
    public void testFire1() throws Exception {
        RuleIntf test = svc.create("RuleService", new HashMap(), RuleIntf.class);
        List list = new ArrayList();
        list.add("elmo");
        list.add("worgie");
        list.add("jess");
        System.out.println( test.execute(list).toString() );
    }
    
    
}
