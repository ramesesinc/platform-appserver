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
public class TestUpdate extends AbstractTestCase {

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

     private Map createContact(String id, String type, String value) {
        Map map = new HashMap();
        map.put("objid", id);
        map.put("type", type);
        map.put("value", value);
        return map;
    }
     
    private Map buildUpdateData() {
        Map data = new HashMap();
        data.put("firstname", "elmo");
        data.put("lastname", "nazareno");
        data.put("entityno", "123456");
        data.put("state", "ACTIVOR");

        Map brgy = new HashMap();
        brgy.put("objid", "BRGY0001");
        brgy.put("name", "POBLACION");

        Map addr = new HashMap();
        //addr.put("objid", "ADDR1");
        addr.put("text", "18 orchid st capitol site");
        addr.put("street", "street 18");
        addr.put("barangay", brgy);
        data.put("address", addr);
        data.put("address2", "capitol tol");
        
        Map info = new HashMap();
        info.put("age", 25);
        info.put("sss", "XXXX-11267899");
        data.put("info", info);
        /*
         Map addr = new HashMap();
         //addr.put("text", "19 orchid st capitol site");
         addr.put("city", "cebu city");
         addr.put("province", "cebu province");
         addr.put("municipality", "dalaguete");
         data.put("address", addr);
         */
        return data;
    }

    private Map getFinder() {
        Map map = new HashMap();
        map.put("entityno", "123456");
        //map.put("state", "ACTIVE");
        return map;
    }

   
    
    
    public void testUpdate() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                Map created = new HashMap();
                created.put("objid", "WVF");

                Map modified = new HashMap();
                modified.put("objid", "EMN");

                Map addr = new HashMap();
                addr.put("street", "1072 dawis");
                addr.put("text", "1072 dawis tabunok talisay city");

                //update info from other tables. 
                Map m = new HashMap();
                m.put("name", "{CONCAT(firstname,',--myname2--',lastname)}");
                //m.put("createdby", created);
                m.put("address", addr);
                m.put("createdby", created);
                m.put("modifiedby", modified);
                m.put("dtcreated", "{NOW()}");

                List items = new ArrayList();
                items.add( createContact("CTCT13c576c5:1533050dbd9:-7ffe", "XMOBILE", "NAZA1234" ) );
                m.put("contactinfos", items);
                
                Map info = new HashMap();
                info.put("age", 23);
                info.put("sss", "YYYY-11267899");
                m.put("info", info);
        
                
                Map whereMap = new HashMap();
                whereMap.put("entityno", "123456");
                em.where("entityno=:entityno", whereMap).update(m);
            }
        });
    }
    
}
