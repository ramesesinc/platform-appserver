/*
 * RuleService.java
 *
 * Created on February 11, 2013, 2:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.rules;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.ContextService;
import com.rameses.rules.common.RuleAction;
import com.rameses.rules.common.RuleRequest.Global;
import com.rameses.rules.common.RuleRequest.RuleFact;
import com.rameses.rules.common.RuleRequest;
import com.rameses.util.URLDirectory;
import com.rameses.util.URLDirectory.URLFilter;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.drools.KnowledgeBase;
import org.drools.definition.type.FactType;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 *
 * @author Elmo
 */
public class RuleService extends ContextService {
    
    private Map<String, RuleContext> ruleContexts = Collections.synchronizedMap(new HashMap());
    
    public Class getProviderClass() {
        return RuleService.class;
    }
    
    private void scanRulesets( Enumeration<URL> urls, final Set set ) {
        while(urls.hasMoreElements()) {
            URL u = urls.nextElement();
            URLDirectory ud = new URLDirectory(u);
            ud.list( new URLFilter(){
                public boolean accept(URL u, String filter) {
                    if(filter.endsWith("/")) {
                        filter = filter.substring( 0, filter.length()-1 );
                        filter = filter.substring( filter.lastIndexOf("/")+1 );
                        set.add( filter );
                    }
                    return false;
                }
            });
        }
    }
    
    //this should be called by the RuleLoader scheduler
    public void loadRules() throws Exception {
        //load all rule contexts...
        Set<String> set = new HashSet();
        if(context instanceof AppContext) {
            AppContext ac = (AppContext)context;
            if(ac.getSharedContext()!=null) {
                scanRulesets( ac.getSharedContext().getClassLoader().getResources( "rulesets" ), set );
            }
        }
        scanRulesets(context.getClassLoader().getResources( "rulesets" ), set);
        for(String s: set) {
            System.out.println("loading rule " + s);
            try {
                getRuleContext(s);
            } catch(Exception e){
                System.out.println("error loading rule "+s + e.getMessage());
            }
        }
    }
    
    public void start() throws Exception {
        
    }
    
    
    public void stop() throws Exception {
    }
    
    public int getRunLevel() {
        return 40;
    }
    
    public RuleContext getRuleContext(String name ) throws Exception {
        if(!ruleContexts.containsKey(name)) {
            RuleContext rc = new RuleContext(name, super.context);
            rc.start();
            ruleContexts.put(name, rc);
        }
        return ruleContexts.get(name);
    }
    
    public void buildRuleContext(String name, List readers ) throws Exception {
        RuleContext rc = new RuleContext(name, super.context);
        rc.build(readers);
        ruleContexts.put(name, rc);
    }
    
    public void addRulePackage( String ruleContextName, Reader reader ) throws Exception {
        RuleContext kbase = getRuleContext(ruleContextName);
        kbase.addRulePackage( reader );
    }
    
   
    
    public void removeRulePackage( String ruleContextName, String packageName ) throws Exception {
        RuleContext kbase = getRuleContext(ruleContextName);
        kbase.removeRulePackage( packageName );
    }
    
    public Collection getRuleContexts() {
        return ruleContexts.values();
    }
    
    public RuleAction createRuleAction() {
        return new RuleAction();
    }
    
    //create facts
    public Object createFact( String ruleset, String name ) throws Exception {
        return createFact(ruleset, name, null);
    }
    
    
    public Object createFact( String ruleset, String name, Map data ) throws Exception {
        KnowledgeBase kbase = getRuleContext( ruleset ).getKnowledgeBase();
        String pkgName = null;
        String factName = name;
        if( name.indexOf(".")>0 ) {
            pkgName = name.substring(0, name.lastIndexOf("."));
            factName = name.substring( name.lastIndexOf(".")+1 );
        }
        FactType ftype = kbase.getFactType( pkgName,factName );
        if( ftype ==null )
            throw new Exception("Fact type " + pkgName + "." + factName + " does not exist");
        Object obj = ftype.newInstance();
        if(data!=null) {
            for(Object m: data.entrySet()) {
                Map.Entry me = (Map.Entry)m;
                try {
                    ftype.set(obj, me.getKey()+"", me.getValue());
                } catch(Exception ign){;}
            }
        }
        return obj;
    }
    
    
    public void execute(RuleRequest request) throws Exception {
        KnowledgeBase kbase = getRuleContext(request.getName()).getKnowledgeBase();
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        try {
            //load facts
            for(RuleFact rf: request.getFacts()) {
                FactType ftype = kbase.getFactType( rf.getPackageName(),rf.getName() );
                Object data = ftype.newInstance();
                ftype.setFromMap( data, rf.getValue() );
                session.insert( data );
            }
            for(Global g: request.getGlobals()) {
                session.setGlobal(g.getName(), g.getValue());
            }
            if( request.getAgenda()!=null) {
                session.getAgenda().getAgendaGroup(request.getAgenda()).setFocus();
            }
            session.fireAllRules();
        } catch(Exception e) {
            throw e;
        } finally {
            session.dispose();
        }
    }
    
    public void execute(String ruleset, List facts, Object globals, String agenda) throws Exception {
        KnowledgeBase kbase = getRuleContext(ruleset).getKnowledgeBase();
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        try {
            //insert facts
            if(facts!=null) {
                for(Object o: facts) {
                    session.insert(o);
                }
            }
            if( globals !=null ) {
                if( globals instanceof RuleAction ) {
                    RuleAction ac = (RuleAction)globals;
                    try {
                        session.setGlobal(ac.getName(), ac);
                    } 
                    catch(Exception ign){
                        System.out.println(ign.getMessage());
                        //ign.printStackTrace();
                    }
                } else if( globals instanceof Map ) {
                    Map g = (Map)globals;
                    for(Object o: g.entrySet()) {
                        Map.Entry me = (Map.Entry)o;
                        try {
                            session.setGlobal( me.getKey()+"", me.getValue() );
                        } catch(Exception ign){
                            System.out.println(ign.getMessage());
                        }
                    }
                } else {
                    System.out.println("warning executing...Globals " + globals.toString() + " is unrecognized");
                }
            }
            
            //set agenda
            if( agenda!=null) {
                session.getAgenda().getAgendaGroup(agenda).setFocus();
            }
            session.fireAllRules();
        } 
        catch(Exception e) {
            throw e;
        } 
        finally {
            session.dispose();
        }
    }
    
    public void execute(String ruleset, List facts) throws Exception {
        execute(ruleset, facts, null, null);
    }
    
    public void execute(String ruleset, List facts, Object globals) throws Exception {
        execute(ruleset,facts,globals,null);
    }
    
    
    //to recover from bad error that cannot be determined yet
    public void removeRuleContext(String ruleset) throws Exception {
        //remove and destroy the rule context.
        RuleContext rc = ruleContexts.remove(ruleset);
        rc.stop();
    }
    
}
