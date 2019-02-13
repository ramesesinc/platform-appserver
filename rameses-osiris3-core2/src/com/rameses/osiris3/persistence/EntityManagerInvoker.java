/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

/**
 *
 * @author dell
 */
public interface EntityManagerInvoker {
    public Object invokeMethod(String methodName, Object[] args);
}
