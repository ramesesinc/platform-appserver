/*
 * ScriptMessageHandler.java
 *
 * Created on February 6, 2013, 9:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.messaging;

import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.script.IScriptRunnableListener;

/**
 *
 * @author Elmo
 * This is used by Messaging
 */
public class ScriptEventMessageHandler extends ScriptMessageHandler implements IScriptRunnableListener {
    
    private String pattern;
    
    /** Creates a new instance of ScriptMessageHandler */
    public ScriptEventMessageHandler(MainContext ctx, String serviceName, String methodName, String expr, String pattern) {
        super(ctx,serviceName,methodName,expr);
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void onBegin() {
    }

    public void onComplete(Object result) {
        System.out.println("doing complete ... " + result);
    }

    public void onRollback(Exception e) {
    }

    public void onClose() {
    }

    public void onCancel() {
    }
    
    public boolean accept(Object data) {
        //check the pattern here.
        System.out.println("checking for the pattern");
        return super.accept(data);
    }
    
    public IScriptRunnableListener getScriptListener() {
        return this;
    }
}
