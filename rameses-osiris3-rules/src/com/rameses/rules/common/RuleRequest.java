/*
 * RuleRequest.java
 *
 * Created on February 12, 2013, 10:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class RuleRequest implements Serializable {
    
    private String name;
    private String agenda;
    private List<RuleFact> ruleFacts = new ArrayList();
    private List<Global> globals = new ArrayList();
    
    /** Creates a new instance of RuleResponse */
    public RuleRequest(String name) {
        this.name = name;
    }
    
    public void addFact(String pkgName, String name, Map data) {
        ruleFacts.add(  new RuleFact(pkgName, name, data ));
    }
    
    public void addGlobal(String name, Object data) {
        globals.add( new Global(name, data));
    }

    public List<RuleFact> getFacts() {
        return ruleFacts;
    }
    
    public List<Global> getGlobals() {
        return globals;
    }

    public String getName() {
        return name;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }
    
    /**************************************************************************
     * classes related to the request
     **************************************************************************/
    public static class RuleFact  {
        private String packageName;
        private String name;
        private Map value;

        public RuleFact(String pkgName, String n, Map d) {
            this.packageName = pkgName;
            this.name = n;
            this.value = d;
        }

        public String getName() {
            return name;
        }

        public Map getValue() {
            return value;
        }

        public String getPackageName() {
            return packageName;
        }
    }
    
    public static class Global {
        private String name;
        private Object value;
        
        public Global(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }

    
}
