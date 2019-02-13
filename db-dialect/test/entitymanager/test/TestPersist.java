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
public class TestPersist extends AbstractTestCase {

    
    public String getDialect() {
        return "mysql";
    }
    
    //private String dialect = "mssql";
    
    private Map createId(String idno, String type) {
        Map map = new HashMap();
        map.put("idno", idno);
        map.put("idtype", type);
        map.put("dateissued", java.sql.Date.valueOf("2014-01-01"));
        return map;
    }

    private Map buildCreateData() {
        Map data = new HashMap();
        data.put("objid", "ENT000001");
        data.put("firstname", "elmo");
        data.put("lastname", "nazareno");
        data.put("name", "nazareno, elmo");
        data.put("entityno", "123456");
        data.put("state", "ACTIVE");
        data.put("type", "INDIVIDUAL");

        Map brgy = new HashMap();
        brgy.put("objid", "BRGY0001");
        brgy.put("name", "POBLACION");

        Map addr = new HashMap();
        //addr.put("objid", "ADDR1");
        addr.put("text", "18 orchid st capitol site");
        addr.put("street", "street 18");
        addr.put("barangay", brgy);
        data.put("address", addr);
        data.put("address2", "capitol 3");

        Map created = new HashMap();
        created.put("objid", "EMN");
        created.put("username", "elmo nazareno");
        data.put("createdby", created);

        Map edited = new HashMap();
        edited.put("objid", "WVF");
        edited.put("username", "worgie flores");
        data.put("modifiedby", edited);
        data.put("billaddress", addr);

        List ids = new ArrayList();
        ids.add(createId("1287787", "Drivers License"));
        ids.add(createId("981288", "SSS"));
        data.put("ids", ids);
        
        Map info = new HashMap();
        info.put("age", 24);
        info.put("sss", "11267899");
        data.put("info", info);
        
        return data;
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

     // TODO add test methods here. The name must begin with 'test'. For example:
    public void ztestCreate() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                em.create(buildCreateData());
            }
        });
    }
    
    public void ztestUpdate() throws Exception {
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

                Map whereMap = new HashMap();
                whereMap.put("entityno", "123456");
                em.where("entityno=:entityno", whereMap).update(m);
            }
        });
    }
    
    public void testRead() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                Map map = new HashMap();
                map.put( "objid", "ENT000001");
                Map d = (Map)em.read(map);
                for( Object m: d.entrySet() ) {
                    Map.Entry me = (Map.Entry)m;
                    System.out.println(me.getKey()+"="+ (me.getValue()==null?"": me.getValue().getClass()));
                }
                System.out.println(d);
            }
        });   
    }

    public void ztestGroupBy() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                em.select("maxname:{MAX(lastname)}, entityno");
                List list = em.find(getFinder()).orderBy("firstname DESC,lastname, entityno").groupBy("entityno, address.barangay.objid, yr:{ YEAR(dtcreated) }").list();
                printList(list);
            }
        });
    }
    
    public void ztestSelect() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                //em.select("address.barangay.name,address_barangay_city:{'cebu city'}, name:{ CONCAT(lastname, ', ', firstname) }, today: {NOW()}");
                List list = em.select( ".*name" ).where("1=1").list();
                printList(list);
            }
        });   
    }
    
     public void ztestSelectSession() throws Exception {
        exec( new ExecHandler() {
            public void execute() throws Exception {
                em.shift("session");
                //em.select("address.barangay.name,address_barangay_city:{'cebu city'}, name:{ CONCAT(lastname, ', ', firstname) }, today: {NOW()}");
                Map m = new HashMap();
                m.put("sessionid", "12222");
                Map z = (Map)em.read(m);
                System.out.print( z );
            }
        });   
    }

    
}
