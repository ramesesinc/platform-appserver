/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ovstest;

import entitymanager.test.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dell.
 */
public class TestOvsCreate extends AbstractTestCase {

    @Override
    public String getDialect() {
        return "mysql";
    }
    
    @Override
    public String getDbname() {
        return "ovr";
    }

    @Override
    public String getEntityname() {
        return "ovs_violation_ticket";
    }

    
    private Map getInfo() {
        Map map = new HashMap();
        //map.put("objid", "VTE-79c361a5:1535a90367c:-7fec");
        map.put("ticketno", "T002");
        map.put("dtcreated", new java.util.Date());
        
        return map;
    }

    public void testSelect() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                em.create(getInfo());
            }
        });   
    }
    
    
}
