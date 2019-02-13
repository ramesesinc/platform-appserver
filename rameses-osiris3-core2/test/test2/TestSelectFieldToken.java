/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test2;

import com.rameses.osiris3.persistence.SelectFieldsTokenizer;
import com.rameses.osiris3.persistence.SelectFieldsTokenizer.Token;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author dell
 */
public class TestSelectFieldToken extends TestCase {
    
    public TestSelectFieldToken(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testTokenizeFields() throws Exception {
        SelectFieldsTokenizer st = new SelectFieldsTokenizer();
        List<Token> list = st.tokenize( "address.*\\s{1,},  lastname, firstname, a:{NOW(a,b)}, {2345678{9,2} >= 99.29}");
        int i = 1;
        for(Token s: list) {
            System.out.println((i++)+". "+ s);
        }
    }
}
