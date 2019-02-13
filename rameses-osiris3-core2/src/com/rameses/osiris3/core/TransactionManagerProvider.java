/*
 * TransactionManagerProvider.java
 *
 * Created on January 30, 2013, 4:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core;

/**
 *
 * @author Elmo
 */
public interface TransactionManagerProvider {
    TransactionManager[] getManagers();
}
