/*
 * ITxnListener.java
 *
 * Created on August 5, 2013, 11:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.services.extended;

/**
 *
 * @author Elmo
 */
public interface ICrudListener {
    
    void beforeCreate(Object data);
    void afterCreate(Object data);
    void beforeUpdate(Object data);
    void afterUpdate(Object data);
    void beforeOpen(Object data);
    void afterOpen(Object data);
    void beforeRemoveEntity(Object data);
    void afterRemoveEntity(Object data);
}
