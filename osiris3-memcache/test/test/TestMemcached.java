/*
 * TestMemcached.java
 * JUnit based test
 *
 * Created on February 10, 2013, 7:56 PM
 */

package test;


import java.io.Serializable;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import junit.framework.*;
import net.spy.memcached.ConnectionObserver;
import net.spy.memcached.MemcachedClient;

/**
 *
 * @author Elmo
 */
public class TestMemcached extends TestCase {
    
    public TestMemcached(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    private class MyObserver implements ConnectionObserver {
        public void connectionEstablished(SocketAddress socketAddress, int i) {
            System.out.println("connection added " );
        }

        public void connectionLost(SocketAddress socketAddress) {
            System.out.println("connection lost ");
        }
        
    }
    
    public class Person implements Serializable {
        private String firstname;
        private String lastname;
        private BigDecimal salary;

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public BigDecimal getSalary() {
            return salary;
        }
        public void setSalary(BigDecimal salary) {
            this.salary = salary;
        }
        public String toString() {
            return lastname + "," + firstname + " salary:" + salary;
        }
    }
    
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        MemcachedClient client =  new MemcachedClient(new InetSocketAddress("localhost", 11211));
        LinkedBlockingQueue queue = new LinkedBlockingQueue();

        /*
        client.add("key5", 20000, "my space0");
        client.append(0, "key5", "my space1");
        client.append(0, "key5", "my space2");
         */       
        client.add("key6", 20000, "data");
        System.out.println("incer"+client.incr("key6", 1));
        System.out.println(client.get("key6"));
        
        
        
    }

}
