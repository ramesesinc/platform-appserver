/*
 * Tester.java
 * JUnit based test
 *
 * Created on January 22, 2013, 5:31 PM
 */

package test2;

import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class Tester extends TestCase {
    
    public Tester(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        /*
        Map map = new HashMap();
        map.put( "app.host", "localhost:8070");
        map.put("app.context", "default");
        ScriptServiceContext ctx = new ScriptServiceContext(map);
        Map env = new HashMap();
        ServiceProxy p = (ServiceProxy)ctx.create( "MyFirstService", env);
        System.out.println( p.invoke( "getInterface" ) );
         */
        System.out.println("env is " + System.getenv("osiris.home"));
    }

}
