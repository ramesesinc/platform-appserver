/*
 * DateServiceTest.java
 * JUnit based test
 *
 * Created on February 19, 2013, 11:14 AM
 */

package system;

import com.rameses.service.ScriptServiceContext;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class AbstractTest extends TestCase {
    
    protected ScriptServiceContext svc;
    
    public AbstractTest() {
        super("Test");
    }
    
    protected void setUp() throws Exception {
        Map conf = new HashMap();
        conf.put("app.cluster", "osiris3");
        conf.put("app.context", "sample");
        conf.put("app.host", "localhost:8070" );
        conf.put("readTimeout", "30000" );
        svc = new ScriptServiceContext(conf);
    }
    
    protected void tearDown() throws Exception {
    }
    
    
    
}
