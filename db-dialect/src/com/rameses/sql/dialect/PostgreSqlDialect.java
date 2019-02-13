/*
 * MySqlDialect.java
 *
 * Created on April 30, 2012, 10:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.sql.dialect;


import com.rameses.osiris3.sql.AbstractSqlDialect;
import com.rameses.osiris3.sql.SqlDialectModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elmo
 */
public class PostgreSqlDialect extends AbstractSqlDialect {
    
    public String getName() {
        return "pgsql";
    }
    
    public String[] getDelimiters() {
        return new String[]{"[","]"};
    }
    
    public String getPagingStatement(String sql, int start, int limit, String[] pagingKeys) {
        return sql + " LIMIT " + limit + " OFFSET " + start;
    }

    public String getUpdateStatement(SqlDialectModel model) {
        final StringBuilder sb = new StringBuilder();
        sb.append( " UPDATE ");
        sb.append( buildListTablesForUpdate(model));
        sb.append( buildUpdateFieldsStatement(model, false) );
        sb.append( " WHERE ");
        List<String> list = new ArrayList();
        buildJoinTablesForUpdate(model, list);
        buildFinderStatement(model, list, true);
        buildSingleWhereStatement(model, list, true);
        sb.append( concatFilterStatement(list));
        return sb.toString();
    }

    public String getDeleteStatement(SqlDialectModel model) {
        StringBuilder sb = new StringBuilder();
        sb.append( " DELETE FROM ");
        sb.append( buildListTablesForUpdate(model));
        sb.append( " WHERE ");
        List<String> list = new ArrayList();
        buildJoinTablesForUpdate(model, list);
        buildFinderStatement(model, list, false);
        buildSingleWhereStatement(model, list, false);
        sb.append( concatFilterStatement(list));
        return sb.toString();
    }
        
    public String getSelectStatement(SqlDialectModel model) {
        String s = super.getSelectStatement(model, true);
        if(model.getStart() >= 0 && model.getLimit()>0 ) {
            s += " LIMIT $P{_limit} OFFSET $P{_start}" ;
        }
        return s;
    }
    
    
}
