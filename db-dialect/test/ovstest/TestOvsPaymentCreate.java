/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ovstest;

import entitymanager.test.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dell.
 */
public class TestOvsPaymentCreate extends AbstractTestCase {

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
        return "ovs_payment";
    }

    private Map createInfo() {
        List items = new ArrayList();
        Map pmt = new HashMap();
        pmt.put("reftype", "cashreceipt");
        pmt.put("refid","RCT00001");
        pmt.put("refno","12345");
        pmt.put("refdate", new java.util.Date());
        pmt.put("amount",200);
        pmt.put("voided", 0);
        pmt.put("items", items);
        Map item = new HashMap();
        item.put("refid", "RTCTCTCT");
        item.put("amount", 200);
        items.add(item);
        return pmt;
    }

    public void testSelect() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                em.setDebug(true);
                em.create(createInfo());
            }
        });   
    }
    
    
}
