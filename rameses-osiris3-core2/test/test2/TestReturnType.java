/*
 * TestReturnType.java
 * JUnit based test
 *
 * Created on May 29, 2014, 3:57 PM
 */

package test2;

import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class TestReturnType extends TestCase {
    
    public TestReturnType(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        System.out.println(MyFile.class.getMethod("setup",null).getReturnType() == void.class);
    }

    public static interface MyFile {
        public Object getData();
        public void setup();
    }
    
}
