/*
 * OsirisRuleTest.java
 * JUnit based test
 *
 * Created on February 12, 2013, 1:05 PM
 */

package test;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.custom.CustomOsirisServer;
import com.rameses.rules.common.RuleRequest;
import com.rameses.osiris3.rules.RuleService;
import com.rameses.rules.common.RuleAction;
import com.rameses.rules.common.RuleActionHandler;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class OsirisRuleTest extends TestCase {
    
    public OsirisRuleTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        OsirisServer svr = new CustomOsirisServer("file:///c:/osiris3_home2",new HashMap());
        AppContext ap = svr.getContext(AppContext.class, "app1");
        System.out.println(ap.getRootUrl());
        System.out.println("***************************************************************");
        System.out.println("start rule test");
        System.out.println("****************************************************************");
        RuleService rsvr = ap.getService(RuleService.class);
        RuleRequest req = new RuleRequest("business");
        Map map = new HashMap();
        map.put( "name", "elmo");
        req.addFact(  "business", "SampleFact", map);
        rsvr.execute( req );
        
        System.out.println("***************************************************************");
        System.out.println("adding new rules");
        System.out.println("***************************************************************");
        InputStream is = getClass().getClassLoader().getResourceAsStream( "rules/rule3.drl" );
        rsvr.addRulePackage( "business", new InputStreamReader(is) );
        rsvr.execute( req );
        
        System.out.println("***************************************************************");
        System.out.println("removing the added rules");
        System.out.println("***************************************************************");
        rsvr.removeRulePackage( "business", "rule3" );
        rsvr.execute( req );
        
        System.out.println("***************************************************************");
        System.out.println("replacing with new rules with same package");
        System.out.println("***************************************************************");
        is = getClass().getClassLoader().getResourceAsStream( "rules/rule4.drl" );
        
        RuleAction action = new RuleAction();
        action.addCommand(  "save", new RuleActionHandler() {
            public void execute(Object context, Object params) {
                System.out.println("context is " + context + " params is " + params);
            }
        });
        System.out.println(action.getName());
        req.addGlobal("actionList", action);
        rsvr.addRulePackage( "business", new InputStreamReader(is) );
        rsvr.execute( req );
        
        
    }

}
