/*
 * DateServiceTest.java
 * JUnit based test
 *
 * Created on February 19, 2013, 11:14 AM
 */

package system;

import java.util.Date;

/**
 *
 * @author Elmo
 */
public class DateServiceTest extends AbstractTest {
    
    private interface DateService {
        Date getServerDate();
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testServerDate() {
        DateService ds = svc.create( "DateService", DateService.class );
        System.out.println(ds.getServerDate());
    }
    
}
