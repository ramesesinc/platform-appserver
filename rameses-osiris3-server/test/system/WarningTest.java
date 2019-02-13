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
public class WarningTest extends AbstractTest {
    
    private interface WarningService {
        Object fire(Object data);
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testPut() {
        try {
            WarningService cs = svc.create( "WarningTest", WarningService.class );
            System.out.println(cs.fire("hello"));
        } catch(Exception e) {
            System.out.println(e.getClass());
        }
    }
    
    
    
}
