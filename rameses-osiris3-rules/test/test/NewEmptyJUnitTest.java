/*
 * NewEmptyJUnitTest.java
 * JUnit based test
 *
 * Created on October 12, 2013, 2:36 PM
 */

package test;

import com.rameses.rules.common.ActionExpression;
import com.rameses.rules.common.EffectiveDate;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
       
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        EffectiveDate date = new EffectiveDate(java.sql.Date.valueOf("2013-12-31"));
        System.out.println(date.getNumericDate());
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        System.out.println("->"+df.format(new Date()));
    }
    
}
