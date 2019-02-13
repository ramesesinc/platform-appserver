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
public class WaterworksQryTest extends AbstractTestCase {

    
    public String getDialect() {
        return "mysql";
    }

    public String getDbname() {
        return "waterworks";
    }
    
     // TODO add test methods here. The name must begin with 'test'. For example:
    public void testQuery() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                em.setDebug(true);
                Map m = new HashMap();
                m.put("n", "APE%");
                em.shift("waterworks_meter").select("serialno,account.acctno,account.acctname").where("1=1");
                
                Map k = new HashMap();
                k.put("currentacctid", "objid");
                //em.shift( "waterworks_meter").leftJoin("waterworks_account", "zz", k).select("serialno,zz.acctno,zz.acctname").where("zz.acctname LIKE :n", m);
                List list = em.list();
                printList(list);
            }
        });   
    }
    
}
