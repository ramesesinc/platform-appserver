/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitymanager.test;

import com.rameses.osiris3.persistence.EntityManagerModel;
import com.rameses.osiris3.persistence.SqlDialectModelBuilder;
import com.rameses.osiris3.sql.SqlDialectModel;
import com.rameses.sql.dialect.MySqlDialect;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dell.
 */
public class TestFindPrimaryKey extends AbstractTestCase {


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

    /*
    public void ztestFindPrimaryKey() throws Exception {
        EntityManagerModel model = em.find(getFinder()).getModel();
        //EntityManagerModel model = em.where("1=1").getModel();
        SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSelectKeysForDelete(model);
        MySqlDialect msd = new MySqlDialect();
        String s = msd.getSelectStatement(sqlModel);
        
        EntityManagerProcessor proc = new EntityManagerProcessor(sqlc,new MySqlDialect());
        List list = proc.createQuery(sqlModel, getFinder(), null).getResultList();
        for( Object o: list ) {
            System.out.println(o);
        }
        System.out.println(s);
    }
    */ 
    
    public void testDelete() throws Exception {
        EntityManagerModel model = em.find(getFinder()).getModel();
        //Map<String,SqlDialectModel> map = SqlDialectModelBuilder.buildDeleteSqlModels(model);
        Collection<SqlDialectModel> list = SqlDialectModelBuilder.buildDeleteSqlModels1(model);
        MySqlDialect msd = new MySqlDialect();
        for(SqlDialectModel sqm: list) {
            String s = msd.getDeleteStatement(sqm);
            System.out.println(s);
        }
    }

    
}
