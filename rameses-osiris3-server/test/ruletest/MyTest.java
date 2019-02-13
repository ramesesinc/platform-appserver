/*
 * MyTest.java
 * JUnit based test
 *
 * Created on February 20, 2013, 2:46 PM
 */

package ruletest;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.custom.CustomOsirisServer;
import com.rameses.osiris3.rules.RuleService;
import com.rameses.rules.common.RuleRequest;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class MyTest extends TestCase {
    
    private RuleService ruleService;
    
    public MyTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        Map conf = new HashMap();
        conf.put( "cluster", "osiris3");
        conf.put( "autoload", false);
        OsirisServer s = new CustomOsirisServer("file:///c:/osiris3_home2", conf );
        s.start();
        AppContext ac = s.getContext( AppContext.class, "sample" );
        ruleService = ac.getService( RuleService.class );
    }
    
    protected void tearDown() throws Exception {
    }
    
    public Map createFact(String name) {
        Map map = new HashMap();
        map.put( "name", name );
        return map;
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        RuleRequest req = new RuleRequest("sample");
        ruleService.execute( req );
    }
    
}
