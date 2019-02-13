/*
 * AbstractTest.java
 * JUnit based test
 *
 * Created on January 30, 2013, 11:01 AM
 */

package test;

import com.rameses.http.HttpClient;
import com.rameses.osiris3.custom.CustomOsirisServer;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class SMSTest extends TestCase {
    
    protected CustomOsirisServer server;
    
    protected String getRootUrl() {
        return "file:///C:/osiris3_test";
    }

    public void testSend() throws Exception {
        HttpClient hc = new HttpClient("www.gazeebu.com/gazeebu-classroom/service/");
        Map map = new HashMap();
        map.put("to", "09063459119");
        map.put("msg", "hello worgie 2 from java client");
        hc.post( "SMSService.send", map );
    }
    
    
}
