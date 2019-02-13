/*
 * Tester.java
 * JUnit based test
 *
 * Created on January 22, 2013, 5:31 PM
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
public class Tester_1 extends TestCase {
    
    public Tester_1(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public interface UpdateTest {
        Object update( Object o );
        
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        Map map = new HashMap();
        map.put( "app.host", "localhost:8070");
        map.put("app.context", "etracs221");
        map.put("app.cluster", "osiris3");
        ScriptServiceContext ctx = new ScriptServiceContext(map);
        Map env = new HashMap();
        UpdateTest ut = ctx.create( "UpdateTest", UpdateTest.class);
        Map d = new HashMap();
        d.put( "objid", "B-4795cca5:13cec253f5f:-7fc5" );
        d.put( "bankname", "LAND BANK OF THE PHILIPPINES 1" );
        d.put( "bankcode", "LBP" );
        d.put( "branchname", "MUNICIPALITY" );
        d.put( "schemaversion", "1.0" );
        d.put( "schemaname", "banki:bank" );
        ut.update( d );
    }

}
