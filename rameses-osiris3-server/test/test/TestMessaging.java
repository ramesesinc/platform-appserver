/*
 * TestMessaging.java
 *
 * Created on February 25, 2013, 12:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package test;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class TestMessaging extends AbstractTestCase {
    
    public interface MessageTest {
        Object send( Object data );
    }
    
    public interface CacheTest {
        Object put(String key, Object params);
        Object get(String key);
    }

    public void testMessaging() throws Exception {
        MessageTest mt = service.create("MessageTest", MessageTest.class);
        Map map = new HashMap();
        map.put("msg", "hello world");
        System.out.println( mt.send( map) );
    }
 
    public void testCache() throws Exception {
        CacheTest mt = service.create("CacheTest", CacheTest.class);
        System.out.println( mt.put("key1", "elmo") );
    }
    
    public void testGetCache() throws Exception {
        CacheTest mt = service.create("CacheTest", CacheTest.class);
        System.out.println( mt.get("key1") );
    }
    
    
}
