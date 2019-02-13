/*
 * DateServiceTest.java
 * JUnit based test
 *
 * Created on February 19, 2013, 11:14 AM
 */

package system;

/**
 *
 * @author Elmo
 */
public class CacheServiceTest extends AbstractTest {
    
    private interface CacheService {
        Object put(String name, Object data);
        Object get(String name);
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void xtestPut() {
        CacheService cs = svc.create( "CacheService", CacheService.class );
        System.out.println(cs.put("first-entry", "Hello friends"));
    }
    
    public void testGet() {
        CacheService cs = svc.create( "CacheService", CacheService.class );
        System.out.println(cs.get("first-entry"));
    }
    
    
}
