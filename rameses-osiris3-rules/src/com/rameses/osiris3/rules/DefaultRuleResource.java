/*
 * DefaultRuleResource.java
 *
 * Created on February 12, 2013, 9:59 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.rules;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.MainContext;
import java.net.URL;

/**
 *
 * @author Elmo
 */
public class DefaultRuleResource extends URLRuleResource {
    
    private void loadHandler( RuleResource.Handler handler, String type ) {
        try {
            //load first if there is shared
            MainContext mc = ruleContext.getMainContext();
            if( mc instanceof AppContext) {
                AppContext ac = (AppContext)mc;
                if( ac.getSharedContext() !=null ) {
                    ClassLoader classLoader = ac.getSharedContext().getClassLoader();
                    URL u = classLoader.getResource( "rulesets/"+ruleContext.getName()+"/"+type);
                    if(u!=null) {
                        super.load( u, classLoader, handler );
                    }
                }
            }
            ClassLoader classLoader = ruleContext.getMainContext().getClassLoader();
            URL u = classLoader.getResource( "rulesets/"+ruleContext.getName()+"/"+type);
            if(u!=null) {
                super.load( u, classLoader, handler );
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        
    }
    
    public void collectFactTypes(RuleResource.Handler handler) {
        loadHandler(handler, "facts");
    }
    
    public void collectRules(RuleResource.Handler handler) {
       loadHandler(handler, "rules");
    }
    
}
