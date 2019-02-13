/*
 * ActionInvoker.java
 *
 * Created on September 15, 2010, 12:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class RuleAction implements Serializable {
    
    public static final String GLOBAL_NAME = "action";
    
    private Map<String, RuleActionHandler> commands = new Hashtable();
    private String name = "action";
    
    private List facts = new ArrayList();
    
    public RuleAction() {
    }
    
    public void addCommand(String name, RuleActionHandler handler ) {
        commands.put(name, handler);
    }
    
    public void execute( String action, Object params ) {
        execute(action, params, null);
    }
    
    public void execute( String action, Object params, Object ruleHandle ) {
        if( !commands.containsKey(action)) {
            System.out.println("Command "+action +" not registered");
            return;
        }
        RuleActionHandler handler = commands.get(action);
        if(handler!=null) {
            handler.execute( params, ruleHandle );
        } 
        else {
            System.out.println("No command found for "+action +". No action executed");
        }
    }

    public void destroy() {
        commands.clear();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getFacts() {
        return facts;
    }

    public void setFacts(List facts) {
        this.facts = facts;
    }

    public Map getCommands() {
        return commands;
    }
    
}
