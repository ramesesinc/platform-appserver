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
public class TestOvsQuery extends AbstractTestCase {

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
        return "ovs_violation_ticket_entry";
    }

    
    
    private Map getFinder() {
        Map map = new HashMap();
        //map.put("objid", "VTE-79c361a5:1535a90367c:-7fec");
        map.put("ticketno", "T002");
        return map;
    }

    public void testSelect() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                Map u = new HashMap();
                u.put("amtpaid", "{amtpaid+:n}");
                
                Map u2 = new HashMap();
                u2.put("n", 13.2);
                
                em.where("parent.ticketno=:ticketno", getFinder()).update(u, u2);
                
                //em.select("parent.violator.objid");
                //Map m = em.where("parent.ticketno=:ticketno", getFinder()).first();
                //System.out.println(m);
            }
        });   
    }
    
    
}
