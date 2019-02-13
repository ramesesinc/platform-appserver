/*
 * DateServiceTest.java
 * JUnit based test
 *
 * Created on February 19, 2013, 11:14 AM
 */

package system;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elmo
 */
public class RuleServiceTest extends AbstractTest {
    
    private interface RuleService {
        Object execute(List list);
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    
    public void testRule() {
        RuleService rs = svc.create( "RuleService", RuleService.class );
        List list = new ArrayList();
        list.add("elmo");
        list.add("worgie");
        rs.execute( list );
    }
    
    
}
