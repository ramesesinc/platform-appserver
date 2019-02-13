/*
 * KeyGenerate.java
 * JUnit based test
 *
 * Created on February 19, 2013, 11:14 AM
 */

package etracsorg; 

import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class KeyGenerate extends TestCase {
    
    public KeyGenerate(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        System.out.println( "BYB"+ ("CTC-01205".hashCode()) ); 
    }

}
