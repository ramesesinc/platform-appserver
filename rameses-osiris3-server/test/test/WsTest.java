/*
 * AbstractTestCase.java
 * JUnit based test
 *
 * Created on February 25, 2013, 12:03 PM
 */

package test;

import com.rameses.http.HttpClient;
import com.rameses.service.ScriptServiceContext;
import java.net.URI;
import junit.framework.*;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

/**
 *
 * @author Elmo
 */
public class WsTest extends TestCase {
    
    private WebSocketClientFactory factory;
    protected ScriptServiceContext service;
    
    protected void setUp() throws Exception {
        factory = new WebSocketClientFactory();
        factory.start();
    }
    
    public class MyHandler implements WebSocket.OnTextMessage {

        public void onMessage(String arg0) {
            System.out.println("messgae received:: " +arg0 );
        }

        @Override
        public void onOpen(Connection cnctn) {
            System.out.println("conn is " + cnctn);
        }

        @Override
        public void onClose(int i, String string) {
            System.out.println("close is " + i + " -> " + string);
        }

    }

    public void testConnect() throws Exception {
         WebSocketClient wsclient = factory.newWebSocketClient();
         wsclient.open(new URI("ws://192.168.254.105:8082/monitor/foobar" ), new MyHandler() );
         
         //javax.swing.JOptionPane.showMessageDialog(null, "hold");
         
         Object t = javax.swing.JOptionPane.showInputDialog("Say Hello");
         HttpClient http = new HttpClient( "192.168.254.105:8082" );
         //Map m = new HashMap();
         //m.put("message", t.toString() );
         http.post("monitor/complete/foobar", t.toString() );
    }
    
    
    
}
