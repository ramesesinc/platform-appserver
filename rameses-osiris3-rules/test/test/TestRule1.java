/*
 * TestRule1.java
 * JUnit based test
 *
 * Created on February 11, 2013, 5:17 PM
 */

package test;


import java.net.URL;
import java.util.Properties;
import junit.framework.*;
import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.type.FactType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 *
 * @author Elmo
 */
public class TestRule1 extends TestCase {
    
    public TestRule1(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        //String langLevel = "1.6";
        //String langLevel = "1.7";
        String langLevel = null;
        final Properties properties = new Properties();
        if(langLevel!=null) {
            properties.setProperty( "drools.dialect.java.compiler.lnglevel",langLevel );
        }
        properties.setProperty( "drools.dialect.java.compiler", "JANINO" );
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(properties);
        KnowledgeBuilder builder  = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
        
        URL u = getClass().getClassLoader().getResource( "rules/rule1.drl");
        builder.add( ResourceFactory.newUrlResource( u ), ResourceType.DRL );
        
        KnowledgeBase kb = builder.newKnowledgeBase();
        FactType ftype = kb.getFactType( "test", "SampleFact" );
        Object fact1 = ftype.newInstance();
        ftype.set(fact1, "name", "elmo");
        
        Object fact2 = ftype.newInstance();
        ftype.set(fact2, "name", "worgie");

        Object fact3 = ftype.newInstance();
        ftype.set(fact3, "name", "sandy");
        
        StatefulKnowledgeSession ks = kb.newStatefulKnowledgeSession();
        ks.insert( fact1 );
        ks.insert( fact2 );
        ks.insert( fact3 );
        ks.fireAllRules();
        ks.dispose();
        
        /**********************************************************************/
        //added new packages in the drools
        System.out.println("***************************************************");
        URL u1 = getClass().getClassLoader().getResource( "rules/rule2.drl");
        builder.add( ResourceFactory.newUrlResource( u1 ), ResourceType.DRL );
        
        kb = builder.newKnowledgeBase();
        FactType ftype1 = kb.getFactType( "test", "SampleFact" );
        Object fact4 = ftype1.newInstance();
        ftype1.set(fact4, "name", "elmo");
        //kb.removeKnowledgePackage("test1");
        
        ks = kb.newStatefulKnowledgeSession();
        ks.insert( fact4 );
        ks.fireAllRules();
        ks.dispose();
        
        
        
    }
    
}
