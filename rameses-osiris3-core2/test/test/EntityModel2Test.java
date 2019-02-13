/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.osiris3.persistence.EntityManagerModel;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaManager;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author dell
 */
public class EntityModel2Test extends TestCase {
    
    public EntityModel2Test(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        SchemaElement elem = SchemaManager.getInstance().getElement("entityindividual");
        EntityManagerModel em = new EntityManagerModel(elem);
        Map brgy = new HashMap();
        brgy.put("name", "Purok 1");
        brgy.put("pin", "123456");
         
        Map addr = new HashMap();
        addr.put("barangay", brgy);
        addr.put("street", "18 orchid st.");
         
        Map m = new HashMap();
        m.put("entityno", "1009210");
        m.put("lastname", "Nazareno");
        m.put("xlastname", "Nazareno");
        m.put("address", addr);
        
        em.getFinders().putAll(m);
        
        Map m1 = new HashMap();
        m1.put("firstname", "elmo");
        em.getFinders().putAll(m1);
         
        
    }
    
    
}
