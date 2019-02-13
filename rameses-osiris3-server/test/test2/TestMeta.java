/*
 * TestMeta.java
 * JUnit based test
 *
 * Created on February 1, 2013, 1:25 PM
 */

package test2;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class TestMeta extends TestCase {
    
    public TestMeta(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    private class Executor {
        public Object execute(String method, Object[] args ) {
            System.out.println("say execute " + method);
            return "ok";
        }
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        GroovyClassLoader gl = new GroovyClassLoader();
        StringBuilder builder = new StringBuilder();
        builder.append( "public class XData { \n" );
        builder.append( "    boolean managed; \n");
        builder.append( "    def executor; \n");
        builder.append( "    public Object invokeMethod(String string, Object args) { \n");
        builder.append( "        return executor.execute(string, args); \n" );
        builder.append( "    } \n");
        
        builder.append( "    void setProperty(String string, Object args) { \n");
        builder.append( "        if(string.startsWith('executor')) {\n" );
        builder.append( "             this.executor = args; }\n");
        builder.append( "        else if(string.startsWith('managed')) {\n" );
        builder.append( "             this.managed = args; }\n");        
        builder.append( "        else { \n");
        builder.append( "           def s = 'set' + fld[0].toUpperCase() + fld[1 ..  fld.length()-1]; \n" );
        builder.append( "           executor.execute(string, args); \n" );
        builder.append( "        } \n");
        builder.append( "    } \n");
      
        builder.append( "    Object getProperty(String fld) { \n");
        builder.append( "       def s = 'get' + fld[0].toUpperCase() + fld[1 ..  fld.length()-1] + '()'; \n" );
        builder.append( "       return executor.execute(s, null); \n");
        builder.append( "    } \n");
        builder.append(" } ");
        try {
            Class metaClass = gl.parseClass( builder.toString() );
            Executor executor = new Executor();
            GroovyObject g = (GroovyObject)metaClass.newInstance();
            g.setProperty( "executor", executor );
            
            GroovyShell s = new GroovyShell();
            s.setProperty( "test", g );
            s.evaluate( "test.currentDate = new java.util.Date();" );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
