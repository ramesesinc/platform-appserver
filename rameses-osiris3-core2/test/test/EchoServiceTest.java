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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class EchoServiceTest extends AbstractTestCase{
    
    public interface EchoService {
        Object test(Object params);
    }
    
    public void testConnect() throws Exception {
        AppContext ctx = server.getContext(AppContext.class, "sample");
        ScriptService svc = ctx.getService(ScriptService.class);
        Map env = new HashMap();
        TransactionContext c = new TransactionContext(server, ctx, env);
        ScriptTransactionManager sm = c.getManager(ScriptTransactionManager.class);
        try {
            EchoService echo = sm.create("EchoService", EchoService.class);
            System.out.println("server date is "+echo.test("hello"));
            c.commit();
        } catch(Exception e) {
            c.rollback();
            throw e;
        } finally {
            c.close();
        }
        Thread.sleep(10000);
    }
}
