/*
 * ResultHandler.java
 *
 * Created on July 21, 2010, 8:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.sql;

import java.sql.ResultSet;
import java.util.List;

/**
 * This provides the implementor the chance to 
 * convert an object to any bean.
 */
public interface FetchHandler {
    
    List start();
    Object getObject(ResultSet rs) throws Exception;
    void end();
}
