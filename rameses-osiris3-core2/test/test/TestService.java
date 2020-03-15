/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author ramesesinc
 */
public class TestService extends TestCase {
    
    public TestService(String testName) {
        super(testName);
    }

    public void test1() throws Exception {
        Map conf = new HashMap();
        conf.put("app.host", "192.168.254.12"); 
        conf.put("app.cluster", "cloud-server"); 
        conf.put("debug", true); 
        
        Map env = new HashMap();
                
        ScriptServiceContext ect = new ScriptServiceContext(conf);
        ServiceProxy p = ect.create("epayment/CloudPaymentService", env); 
        Object res = p.invoke("getUnpostedPaymentList", new Object[]{}); 
        System.out.println( res );
    }
}
