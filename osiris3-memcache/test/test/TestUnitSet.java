/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.util.KeyGen;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

/**
 *
 * @author rameses
 */
public class TestUnitSet extends TestCase {

    private MemcachedClient client; 
    
    public TestUnitSet(String testName) {
        super(testName);
    }

    public void testPut() throws Exception {
        Map data = new HashMap();
        data.put("name", "juan dela cruz");
        data.put("address", "cebu city");
        
        List<InetSocketAddress> addrs = AddrUtil.getAddresses("192.168.254.16:11211"); 
        MemcachedClient client = new MemcachedClient( addrs ); 
        client.set("test1", 30, data).get(); 
    }    
}
