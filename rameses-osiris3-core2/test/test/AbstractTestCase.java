/*
 * AbstractTest.java
 * JUnit based test
 *
 * Created on January 30, 2013, 11:01 AM
 */

package test;

import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.custom.CustomOsirisServer;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class AbstractTestCase extends TestCase {
    
    protected CustomOsirisServer server;
    
    protected String getRootUrl() {
        return "file:///C:/osiris3_test";
    }

    protected void setUp() throws Exception {
        
        Map map = new HashMap();
        map.put("cluster", "osiris3");
        server = new CustomOsirisServer(getRootUrl(),map);
        OsirisServer.setInstance(server);
        
    }
    
    
}
