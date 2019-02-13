/*
 * FactFilter.java
 *
 * Created on June 11, 2013, 10:16 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

/**
 *
 * @author Elmo
 * This interface controls what we insert into the facts collection
 * It returns a set of return values
 * 
 * 0 = resumes next, do nothing and continue
 * 1 = retain existing state and exit loop
 * 2 = log retain existing state and exit loop. Log means to log the rule that executed
 * 3 = swap the old with the new value and break loop
 * 4 = log swap the old with the new value and break loop. Log the rule that executed
 */
public interface FactFilter {
    public static int RESUME_NEXT = 0;
    public static int RETAIN_AND_BREAK = 1;
    public static int LOG_RETAIN_AND_BREAK = 2;
    public static int SWAP_AND_BREAK = 3;
    public static int LOG_SWAP_AND_BREAK = 4;
    
    int compare( Object oldValue, Object newValue );
}
