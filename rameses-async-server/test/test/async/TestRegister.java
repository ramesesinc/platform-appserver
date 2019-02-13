/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on May 27, 2014, 4:35 PM
 */

package test.async;

import com.rameses.http.HttpClient;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class TestRegister extends TestCase {
    
    public TestRegister(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        Map data = new HashMap();
        data.put("id", "xxx");
        
        HttpClient c = new HttpClient("localhost:8050", true);
        Object o = c.post("async/register", new Object[]{data}); 
        System.out.println("register-> " + o);
    } 
}
