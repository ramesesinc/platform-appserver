/*
 * ITxnListener.java
 *
 * Created on August 5, 2013, 11:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.services.extended;

import java.util.List;

/**
 *
 * @author Elmo
 */
public interface IListListener {
    
    void beforeList(Object data);
    void afterList(Object params, Object list);
}
