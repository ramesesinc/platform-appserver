/*
 * ComplexSqlParserTest.java
 * JUnit based test
 *
 * Created on August 7, 2013, 1:02 PM
 */

package test2;

import com.rameses.osiris3.core.support.ComplexSqlParser;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class ComplexSqlParserTest extends TestCase {
    
    public ComplexSqlParserTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception{
        ComplexSqlParser cq = new ComplexSqlParser();
        /*
        String s = "@(100)[select * from sys_user u where u.objid=$P{PARAMS.objid}].each {"+
                  "  @bpls[select * from sys_org og WHERE userid=$P{it.objid}].collect{ [name:it.name] } ";  
        
        System.out.println(cq.parseStatement(s,"main"));    
        System.out.println(cq.parseStatement("select * from sys_user"));
         */
        String s = "SELECT * FROM sys_user where objid='cat'";
        System.out.println(cq.parseStatement(s,"main"));    
    }

}
