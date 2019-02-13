import java.util.concurrent.LinkedBlockingQueue;
import junit.framework.*;
/*
 * TestQueue.java
 * JUnit based test
 *
 * Created on January 21, 2013, 3:25 PM
 */

/**
 *
 * @author Elmo
 */
public class TestQueue extends TestCase {
    
    public TestQueue(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    public class MyQueue extends LinkedBlockingQueue {
        public MyQueue(int sz) {
            super(sz);
        }
        public boolean add(Object e) {
            try {
                return super.add(e);
            } catch(Exception ex) {
                super.poll();
                return super.add(e);
            }
        }
        
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        MyQueue list = new MyQueue(5);
        list.add("one");
        list.add("two");
        list.add("three");
        list.add("four");
        list.add("five");
        list.add("six");
        for(Object s: list) {
            System.out.println(s);
        }
    }
    
}
