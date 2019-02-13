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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class AsyncTest extends TestCase {
    
    private ScriptServiceContext svc;
    
    public AsyncTest(String testName) {
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
    
    private interface TestIntf {
        Map sayTest(Map data);
        String getStringInterface();
        Object sendMessage( Object data );
        Object sendMessage( Object data, AsyncHandler handler );
    }
    
    public void testSendMessage() throws Exception {
        TestIntf test = svc.create("MyFirstService", new HashMap(), TestIntf.class);
        Map m = new HashMap();
        m.put("objid","EMN3");
        m.put("name","ELMOKIXss");
        m.put("firstnam1e","DANNY");
        m.put("lastname","BONNY");
        
        final LinkedBlockingQueue queue = new LinkedBlockingQueue();
        try {
            test.sendMessage( m, new AsyncHandler(){
//                public void onMessage(AsyncResponse o) {
//                    if(o.getStatus()==AsyncResponse.PROCESSING) {
//                        Object val = null;
//                        while((val=o.getNextValue())!=null) {
//                            System.out.println("received value->"+val);
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
            Object answer = queue.poll(30,TimeUnit.SECONDS);
            System.out.println("RESULT IS ->"+answer);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
