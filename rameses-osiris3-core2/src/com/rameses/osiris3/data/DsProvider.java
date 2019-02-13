/*
 * DsProvider.java
 *
 * Created on February 1, 2013, 8:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.data;

import java.util.Map;

/**
 *
 * @author Elmo
 */
public interface DsProvider {
    
    AbstractDataSource createDataSource(String name, Map map);
    
}
