/*
 * SqlDialect.java
 *
 * Created on April 30, 2012, 9:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.sql;

/**
 *
 * @author Elmo.
 * added new methods ->
 */
public interface SqlDialect {
    
    String[] getDelimiters();
    
    String getName();
    String getPagingStatement( String sql, int start, int limit, String [] pagingKeys  );
    
    String getCreateStatement( SqlDialectModel model ) throws Exception;
    String getUpdateStatement( SqlDialectModel model ) throws Exception;
    String getReadStatement( SqlDialectModel model ) throws Exception;
    String getDeleteStatement( SqlDialectModel model ) throws Exception;
    String getSelectStatement( SqlDialectModel model ) throws Exception;
    
    SqlDialectFunction getFunction(String funcName);
    
    
}
