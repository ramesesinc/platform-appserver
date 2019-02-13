/*
 * TestConnect.java
 * JUnit based test
 *
 * Created on February 27, 2013, 9:37 AM
 */

package test;

import com.rameses.osiris3.script.InvokerProxy;
import com.rameses.service.ScriptServiceContext;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class TestLOB extends TestCase {
    
    public TestLOB(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    interface LOBService {
        Object getList( Object searchText, Object params );
        String getStringInterface();
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        Map map = new HashMap();
        map.put("app.cluster","osiris3");
        map.put("app.context","etracs221");
        map.put("app.host","localhost:8070");
        ScriptServiceContext sc = new ScriptServiceContext(map);
        LOBService lobs = sc.create( "LOBService", LOBService.class );
        System.out.println(lobs.getStringInterface());
        System.out.println("value ->"+lobs.getList( "hello", null ));
        
        
    }
    
}
