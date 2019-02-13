/*
 * DateFactTest.java
 * JUnit based test
 *
 * Created on July 15, 2013, 9:11 AM
 */

package facts;

import com.rameses.rules.common.ActionExpression;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class DateFactTest extends TestCase {
    
    public DateFactTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        Map map = new HashMap();
        map.put("YEAR",1);
        ActionExpression ae = new ActionExpression("'Hello friends and ' + YEAR", map);
        System.out.println(ae.getStringValue());
    }
    
}
