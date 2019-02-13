/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on May 27, 2014, 4:35 PM
 */

package test.async;

import com.rameses.http.HttpClient;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author compaq
 */
public class TestSend extends TestCase {
    
    public TestSend(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    public void testMain() throws Exception {
        Map data = new HashMap();
        data.put("id", "xxx");
        data.put("refid", new UID().toString());         
        data.put("data", "My sample data " + new java.sql.Timestamp(System.currentTimeMillis())); 
        
        HttpClient c = new HttpClient("localhost:8050", true);
        Object o = c.post("async/send", new Object[]{data}); 
        System.out.println("send-> " + o);
    } 
}
