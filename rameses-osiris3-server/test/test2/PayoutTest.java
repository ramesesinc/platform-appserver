/*
 * TestServer.java
 * JUnit based test
 *
 * Created on January 8, 2013, 3:41 PM
 */

package test2;

import com.rameses.common.AsyncHandler;
import com.rameses.common.AsyncResponse;
import com.rameses.service.ScriptServiceContext;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class PayoutTest extends TestCase {
    
    private ScriptServiceContext svc;
    
    public PayoutTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        Map conf = new HashMap();
        conf.put("app.cluster", "osiris3");
        conf.put("app.context", "app1");
        conf.put("app.host", "localhost:8070" );
        conf.put("readTimeout", "30000" );
        svc = new ScriptServiceContext(conf);
    }
    
    protected void tearDown() throws Exception {
    }
    
    private interface PayoutIntf {
        Object payoutSearch( Object data, AsyncHandler handler );
    }
    
    public void testPayout() throws Exception {
        PayoutIntf test = svc.create("PayoutService", new HashMap(), PayoutIntf.class);
        Map m = new HashMap();
        m.put("objid","EMNAZARENO");
        
        final LinkedBlockingQueue queue = new LinkedBlockingQueue();
        final List resultList = new ArrayList();
        try {
            test.payoutSearch( m, new AsyncHandler(){
//                public void onMessage(AsyncResponse o) {
//                    if(o.getStatus()==AsyncResponse.PROCESSING) {
//                        Object val = null;
//                        while((val=o.getNextValue())!=null) {
//                            System.out.println("received value->"+val);
//                            resultList.add( val );
//                        }
//                    }
//                    else if( o.getStatus() == AsyncResponse.COMPLETED ) {
//                        System.out.println("asyn COMPLETED!");
//                        queue.add( "ok" );
//                    }
//                }
                public void onError(Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onMessage(Object o) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void call(Object o) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });
            
            //block until result arrived
            Object x = queue.poll(60,TimeUnit.SECONDS);
            System.out.println("YOUR RESULT....");
            for(Object o: resultList ) {
                System.out.println(o);
            }
            
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
