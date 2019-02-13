/*
 * DateServiceTest.java
 * JUnit based test
 *
 * Created on February 19, 2013, 11:14 AM
 */

package system;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class TemplateServiceTest extends AbstractTest {
    
    
    private interface TemplateService {
        Object get(String name, Object data);
    }
    
    public void testGet() {
        TemplateService cs = svc.create( "TemplateService", TemplateService.class );
        Map map = new HashMap();
        map.put("firstname", "elmo");
        System.out.println(cs.get("first-entry", map));
    }
    
    
}
