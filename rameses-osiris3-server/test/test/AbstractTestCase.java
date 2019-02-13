/*
 * AbstractTestCase.java
 * JUnit based test
 *
 * Created on February 25, 2013, 12:03 PM
 */

package test;

import com.rameses.service.ScriptServiceContext;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class AbstractTestCase extends TestCase {
    
    protected ScriptServiceContext service;
    
    protected void setUp() throws Exception {
        Map conf = new HashMap();
        conf.put("app.cluster", "osiris3");
        conf.put("app.context", "sample");
        conf.put("app.host", "localhost:8070" );
        conf.put("readTimeout", "30000" );
        service = new ScriptServiceContext(conf);
    }

}
