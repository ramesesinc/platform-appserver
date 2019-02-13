/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.net.InetSocketAddress;
import java.util.List;
import junit.framework.TestCase;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

/**
 *
 * @author rameses
 */
public class TestUnitRemove extends TestCase {

    private MemcachedClient client; 
    
    public TestUnitRemove(String testName) {
        super(testName);
    }

    public void testPut() throws Exception {
        List<InetSocketAddress> addrs = AddrUtil.getAddresses("192.168.254.16:11211"); 
        MemcachedClient client = new MemcachedClient( addrs ); 
        client.delete("test1").get();
    }    
}
