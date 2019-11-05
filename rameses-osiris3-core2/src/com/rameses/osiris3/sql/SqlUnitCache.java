/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.sql;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dell
 */
public class SqlUnitCache {
    
    private static int MAX_CACHE_SIZE = 5000;    
    private static Map<Integer, SqlUnit> sqlUnits = Collections.synchronizedMap(new HashMap());
    
    
    public static SqlUnit getSqlUnit( SqlDialectModel model, SqlDialect dialect ) throws Exception {
        Integer keyidx = model.getId();
        SqlUnit su = sqlUnits.get( keyidx ); 
        if ( su != null ) return su; 

        String action = model.getAction();
        String statement = null;
        if(action.equals("create")) {
            statement = dialect.getCreateStatement(model);
        }
        else if( action.equals("update")) {
            statement = dialect.getUpdateStatement(model);
        }
        else if( action.equals("select")) {
            statement = dialect.getSelectStatement(model);
        }
        else if( action.equals("delete")) {
            statement = dialect.getDeleteStatement(model);
        }
        
        su = new SqlUnit( statement );
        if ( action.equals("select")) return su; 
        
        if ( sqlUnits.size() > MAX_CACHE_SIZE ) { 
            System.out.println("Clearing SQL Unit Cache... (MAX_CACHE_SIZE = "+ MAX_CACHE_SIZE +")");
            sqlUnits.clear(); 
        }
            
        sqlUnits.put( keyidx, su );
        return su; 
    }
    
    public static void clear() { 
        sqlUnits.clear(); 
    } 
}
