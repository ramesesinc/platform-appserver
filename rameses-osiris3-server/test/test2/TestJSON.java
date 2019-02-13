/*
 * TestJSON.java
 * JUnit based test
 *
 * Created on January 16, 2013, 4:13 PM
 */

package test2;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import junit.framework.*;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.util.ajax.JSON.Convertor;

/**
 *
 * @author Elmo
 */
public class TestJSON extends TestCase {
    
    public TestJSON(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        
        JSON.registerConvertor( Date.class, new Convertor(){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            public void toJSON(Object object, JSON.Output output) {
                String dt = sf.format( (Date)object );
                output.add( dt );
            }
            public Object fromJSON(Map map) {
                return new Date();
            }
        });
        
        
        Map map = new HashMap();
        map.put( "name", "elmo" );
        map.put( "dtfiled",new Date() );
        map.put( "amount",new BigDecimal(200.01) );
        
        System.out.println("------------------------");
        //String s = JsonUtil.toString(map);
        String s = JSON.toString( map );
        
        Map p = (Map)JSON.parse(s);
        System.out.println("result....");
        System.out.println(p);
        for(Object m: p.entrySet()) {
            Map.Entry me = (Map.Entry)m;
            System.out.println(me.getKey()+" " +me.getValue().getClass());
        }
        
    }
    
    class MyConvertor implements Convertor {
        
        public void toJSON(Object object, JSON.Output output) {
            
        }

        public Object fromJSON(Map map) {
            return null;
        }
        
    }

}
