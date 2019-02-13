/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitymanager.test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dell.
 */
public class TestDelete extends AbstractTestCase {

    
    @Override
    public String getDialect() {
        return "mysql";
    }

    private Map getFinder() {
        Map map = new HashMap();
        map.put("entityno", "123456");
        //map.put("state", "ACTIVE");
        return map;
    }

    public void testDelete()  throws Exception {
        try {
            em.find(getFinder());
            em.delete();
            cm.commit();
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            cm.close();
        }
    }

    
    
}
