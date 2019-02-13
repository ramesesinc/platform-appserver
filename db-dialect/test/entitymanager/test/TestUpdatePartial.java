/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitymanager.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dell.
 */
public class TestUpdatePartial extends AbstractTestCase {

    public String getDialect() {
        return "mysql";
    }
    
    private Map createId(String idno, String type) {
        Map map = new HashMap();
        map.put("idno", idno);
        map.put("idtype", type);
        map.put("dateissued", java.sql.Date.valueOf("2014-01-01"));
        return map;
    }

     // TODO add test methods here. The name must begin with 'test'. For example:
    public void testUpdate() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                Map finder = new HashMap();
                finder.put("entityno","123456" );
                
                List addedIds = new ArrayList();
                addedIds.add( createId("98192189", "voters id" ) );
                addedIds.add( createId("1989-0001", "school id") );
                Map m = createId("999", "Drviers Licensia");
                m.put("objid", "ID3a4404d5:15353f918c8:-7ffd");
                addedIds.add( m );
                
                Map d = new HashMap();
                d.put("ids", addedIds);
                
                em.find(finder).update(d);
            }
        });
    }

    
}
