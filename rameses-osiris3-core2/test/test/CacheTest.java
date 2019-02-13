/*
 * EchoServiceTest.java
 *
 * Created on February 24, 2013, 8:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package test;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.script.ScriptService;
import com.rameses.osiris3.script.ScriptTransactionManager;
import groovy.sql.Sql;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class CacheTest extends AbstractTestCase{
    
    public interface CacheTestIntf  {
        Object put(String key, Object params);
        Object get(String key);
    }
    
    public void testConnect() throws Exception {
        AppContext ctx = server.getContext(AppContext.class, "sample");
        ScriptService svc = ctx.getService(ScriptService.class);
        Map env = new HashMap();
        TransactionContext c = new TransactionContext(server, ctx, env);
        ScriptTransactionManager sm = c.getManager(ScriptTransactionManager.class);
        try {
            CacheTestIntf cache = sm.create("CacheTest", CacheTestIntf.class);
            System.out.println("cache value is "+cache.put("key", "hello"));
            c.commit();
        } catch(Exception e) {
            c.rollback();
            throw e;
        } finally {
            c.close();
        }
        
    }
}
