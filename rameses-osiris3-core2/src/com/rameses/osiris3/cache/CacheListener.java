/*
 * CacheListener.java
 *
 * Created on February 9, 2013, 8:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.cache;

/**
 *
 * @author Elmo
 */
public interface CacheListener {
    void onAdd(String id);
    void onRemove(String id);
}
