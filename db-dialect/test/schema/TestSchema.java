/*
 * TestSchema.java
 * JUnit based test
 *
 * Created on April 6, 2014, 8:08 PM
 */

package schema;

import com.rameses.osiris3.schema.Schema;
import com.rameses.osiris3.schema.SchemaManager;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class TestSchema extends TestCase {
    
    public TestSchema(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        System.out.println("hello test");
        Schema s = SchemaManager.getInstance().getSchema("customer");
        System.out.println(s.getName());
        System.out.println("schema is " + s);
    }

}
