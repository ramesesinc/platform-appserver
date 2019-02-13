/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitymanager.test;

import com.rameses.sql.dialect.functions.mysql.FORMAT_SERIES;
import junit.framework.TestCase;

/**
 * @author dell.
 */
public class TestFunction extends TestCase {

     // TODO add test methods here. The name must begin with 'test'. For example:
    public void testCreate() throws Exception {
        FORMAT_SERIES d = new FORMAT_SERIES();
        d.addParam("startseries");
        d.addParam("af.serieslength");
        d.addParam("prefix");
        d.addParam("suffix");
        System.out.println(d.toString());
    }

    
}
