/*
 * TestConnect.java
 * JUnit based test
 *
 * Created on February 27, 2013, 9:37 AM
 */

package test;

import com.rameses.http.HttpClient;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class TestConnect extends TestCase {
    
    public TestConnect(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        HttpClient hc = new HttpClient("192.168.254.4:8080/test");
        hc.setOutputHandler( hc.STRING_OUTPUT );
        Map map = new HashMap();
        map.put("name", "elmo");
        String s = "{msg:\"cat is black\"}";
        System.out.println( com.rameses.osiris3.server.JsonUtil.toObject( s ).getClass() );
    }
    
}
