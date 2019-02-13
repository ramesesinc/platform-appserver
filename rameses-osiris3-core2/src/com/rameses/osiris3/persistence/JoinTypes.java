/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

/**
 *
 * @author dell
 */
public interface JoinTypes {
    
    public static String EXTENDED = "extended";         //extended object 
    public static String ONE_TO_MANY = "one-to-many";   //parent-children
    public static String MANY_TO_ONE = "many-to-one";   //attach existing objects
    public static String ONE_TO_ONE = "one-to-one";     //embedded object same as extends
    
}
