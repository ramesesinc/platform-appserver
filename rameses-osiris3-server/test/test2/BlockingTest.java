/*
 * TestServer.java
 * JUnit based test
 *
 * Created on January 8, 2013, 3:41 PM
 */

package test2;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class BlockingTest extends TestCase {
    
    public BlockingTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
       
    }
    
    protected void tearDown() throws Exception {
    }
    
    public void testSendMessage() throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future f = service.submit(new Callable(){
            public Object call() throws Exception {
                Thread.sleep(5000);
                return "This is result";
            }
        });
        System.out.println(f.get(10, TimeUnit.SECONDS));
    }
    
}
