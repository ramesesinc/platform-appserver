/*
 * RuleContext.java
 *
 * Created on February 12, 2013, 9:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.rules;

import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.util.Service;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;

/**
 *
 * @author Elmo
 */
public class RuleContext {
    
    private MainContext mainContext;
    private KnowledgeBase knowledgeBase;
    private List<RuleResource> providers;
    private String name;
    private KnowledgeBuilderConfiguration conf;
    
    public RuleContext(String name, MainContext m) {
        this.name = name;
        this.mainContext = m;
        
        String langLevel = (String) this.mainContext.getConf().get("drools.langLevel");
        Properties properties = new Properties();
        if(langLevel!=null) {
            properties.setProperty( "drools.dialect.java.compiler.lnglevel",langLevel );
        }
        properties.setProperty( "drools.dialect.java.compiler", "JANINO" );
        conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(properties,this.mainContext.getClassLoader());
    }
    
    public void build(List<Reader> readers) throws Exception {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
        for(Reader r: readers) {
            builder.add( ResourceFactory.newReaderResource(r), ResourceType.DRL );
        }
        if(builder.hasErrors()) {
            KnowledgeBuilderErrors  errs = builder.getErrors();
            StringBuffer sb = new StringBuffer();
            if(errs.size()>0) {
                for(KnowledgeBuilderError e: errs) {
                    sb.append( e.getMessage() + "\n");
                }
            }
            throw new Exception(sb.toString());
        }
        KnowledgeBaseConfiguration _conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null,this.mainContext.getClassLoader());
        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( _conf );
        knowledgeBase.addKnowledgePackages( builder.getKnowledgePackages() );
    }
    
    public void start() throws Exception {
        //load the resource providers
        providers = new ArrayList();
        Iterator<RuleResource> iter = Service.providers( RuleResource.class, OsirisServer.class.getClassLoader() );
        while(iter.hasNext()) {
            RuleResource res = iter.next();
            res.setRuleContext( this );
            providers.add( res);
        }
        initKnowledgeBuilder();
    }
    
    public void stop() {
    }
    
    //this is called when starting up the rules
    public void initKnowledgeBuilder() throws Exception {
        final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
        RuleResource.Handler handler = new RuleResource.Handler() {
            public void handle(InputStream is) {
                builder.add( ResourceFactory.newInputStreamResource( is ), ResourceType.DRL );
            }
        };
        //build the facts
        for(RuleResource r: providers) {
            r.collectFactTypes( handler );
        }
        
        //build the rules
        for(RuleResource r: providers) {
            r.collectRules( handler );
        }
        knowledgeBase = builder.newKnowledgeBase();
    }
    
    public String getName() {
        return name;
    }
    
    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }
    
    public MainContext getMainContext() {
        return mainContext;
    }
    
    public void addRulePackage(Reader reader) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( knowledgeBase, conf );
        kbuilder.add( ResourceFactory.newReaderResource(reader) , ResourceType.DRL );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        if(kbuilder.hasErrors()) {
            KnowledgeBuilderErrors  errs = kbuilder.getErrors();
            StringBuffer sb = new StringBuffer();
            if(errs.size()>0) {
                for(KnowledgeBuilderError e: errs) {
                    sb.append( e.getMessage() + "\n");
                }
            }
            throw new Exception(sb.toString());
        }
    }
    
    public void removeRulePackage(String packageName) throws Exception {
        knowledgeBase.removeKnowledgePackage( packageName );
    }
    
    
    
}
