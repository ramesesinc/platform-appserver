/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.osiris3.server.JSON;
import java.util.Date;
import java.util.HashMap;
import junit.framework.TestCase;

/**
 *
 * @author ramesesinc
 */
public class NewEmptyJUnitTest extends TestCase {
    
    public NewEmptyJUnitTest(String testName) {
        super(testName);
    }

    public void test1() throws Exception {
        HashMap map = new HashMap();
        map.put("datetime", new Date( System.currentTimeMillis())); 
        map.put("date", new java.sql.Date( System.currentTimeMillis())); 
        
        System.out.println( new JSON().encode(map));
    }    
}
