/*
 * RuleResource.java
 *
 * Created on February 12, 2013, 9:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.rules;

import com.rameses.osiris3.core.MainContext;
import java.io.InputStream;

/**
 *
 * @author Elmo
 */
public interface RuleResource {
    
    void setRuleContext(RuleContext ctx);
    void collectFactTypes( Handler handler );
    void collectRules( Handler handler );
    
    public static interface Handler {
        void handle(InputStream is);
    }
    
}
