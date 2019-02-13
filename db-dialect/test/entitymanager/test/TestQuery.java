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
public class TestQuery extends AbstractTestCase {

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
                map.put("addr2", "%capitol%");
                em.select("today,fullname,address.barangay.name,address_barangay_city:{'cebu city'}, name:{ CONCAT(lastname, ',', firstname) }, today: {NOW()}");
                List list = em.where("address2 like :addr2 and 1=1", map ).orderBy("address.barangay.name").limit(2).list();
                //List list = em.select( ".*" ).where("address2 = :addr2", map).list();
                printList(list);
                
                //Map m = em.select("count:{1}").where("1=1").first();
                //System.out.println(m);
            }
        });   
    }
    
    
}
