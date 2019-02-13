/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test2;

import com.rameses.osiris3.persistence.EntityManager;
import com.rameses.osiris3.schema.SchemaManager;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author dell
 */
public class TestGetSchema extends TestCase {
    
    private SchemaManager schemaManager;
    private EntityManager em;
    
    public TestGetSchema(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        schemaManager = SchemaManager.getInstance();
        //em = new EntityManager(schemaManager, "entityindividual");
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() {
        Map map = em.getSchema();
        for( Object k: (List) map.get("fields") ) {
            System.out.println(k);
        }
        
    }
}
