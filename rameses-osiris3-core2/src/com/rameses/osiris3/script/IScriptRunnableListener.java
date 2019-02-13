/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.script;

/**
 *
 * @author dell
 */
public interface IScriptRunnableListener {
    public void onBegin();
    public void onComplete(Object result);
    public void onRollback(Exception e);
    public void onClose();
    public void onCancel();
}
