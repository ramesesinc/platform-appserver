/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitymanager.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dell.
 */
public class TestQuery1 extends AbstractTestCase {

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

    public void testSelect() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                Map map = new HashMap();
                map.put("address.barangay.name", "poblacion");
                List list = em.select("lastname,firstname").find(map).list();
                
                printList(list);
                
                //Map m = em.select("count:{1}").where("1=1").first();
                //System.out.println(m);
            }
        });   
    }
    
    
}
