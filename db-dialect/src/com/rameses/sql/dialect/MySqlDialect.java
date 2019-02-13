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
public class MySqlDialect extends AbstractSqlDialect {
    
    public String getName() {
        return "mysql";
    }
    
    public String[] getDelimiters() {
        return new String[]{"`","`"};
    }
    
    public String getPagingStatement(String sql, int start, int limit, String[] pagingKeys) {
        return sql + " LIMIT " + start + "," + limit;
    }

    public String getUpdateStatement(SqlDialectModel model) {
        final StringBuilder sb = new StringBuilder();
        sb.append( " UPDATE ");
        sb.append( buildListTablesForUpdate(model));
        sb.append( buildUpdateFieldsStatement(model, true) );
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
        sb.append( getDelimiters()[0]+ model.getTablename()+getDelimiters()[1] );
        sb.append( " WHERE ");
        List<String> list = new ArrayList();
        buildJoinTablesForUpdate(model, list);
        buildFinderStatement(model, list, false);
        buildSingleWhereStatement(model, list, false);
        sb.append( concatFilterStatement(list));
        return sb.toString();
    }
        
    public String getSelectStatement(SqlDialectModel model) {
        //union filters 
        StringBuilder sb = new StringBuilder();
        sb.append( super.getSelectStatement(model, true));
        if(model.getStart() > 0 || model.getLimit()>0 ) {
            sb.append( " LIMIT $P{_start}, $P{_limit}" );
        }
        return sb.toString();
    }
    

}
