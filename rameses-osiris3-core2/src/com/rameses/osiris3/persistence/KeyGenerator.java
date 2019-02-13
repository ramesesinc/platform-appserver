/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

/**
 *
 * @author dell
 */
public interface KeyGenerator {
    String getNewKey(String prefix, int len);
}
