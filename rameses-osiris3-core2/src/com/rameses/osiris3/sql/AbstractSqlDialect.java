/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.sql;


import com.rameses.osiris3.schema.AbstractSchemaView;
import com.rameses.osiris3.schema.LinkedSchemaView;
import com.rameses.osiris3.schema.SchemaViewRelationField;
import com.rameses.osiris3.sql.SqlDialectModel.Field;
import com.rameses.osiris3.sql.SqlDialectModel.WhereFilter;
import com.rameses.util.ValueUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author elmo
 */
public abstract class AbstractSqlDialect implements SqlDialect {
    
    private int version; 
    
    public int getVersion() { return version; } 
    public void setVersion( int version ) {
        this.version = version; 
    } 
    
    public SqlDialectFunction getFunction(String funcName) {
        return SqlFunctionProvider.getFunction(funcName, this.getName());
    }

    /**
     * This method joins statements using an AND statement
     */
    protected String concatFilterStatement(List<String> filters) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String s : filters) {
            if (s == null || s.trim().length() <= 0) {
                continue;
            }
            if (i++ > 0) {
                sb.append(" AND ");
            }
            sb.append(s);
        }
        return sb.toString();
    }

    protected String fixStatement(SqlDialectModel sqlModel, String expr, boolean includeFieldAlias ) {
        try {
            return SqlExprParserUtil.translate(sqlModel, expr, this, includeFieldAlias);
        } catch (Exception e) {
            System.out.println("Error in fix statement. " + e.getMessage());
            return null;
        }
    }
    
    protected String fixWhereStatement(SqlDialectModel sqlModel, SqlDialectModel.WhereFilter whereFilter, boolean withAlias) {
        try {
            return SqlExprParserUtil.translate(sqlModel, whereFilter.getExpr(), this, withAlias);
        } catch (Exception e) {
            System.out.println("Error in where statement. " + e.getMessage());
            return null;
        }
    }

    protected void buildSingleWhereStatement(SqlDialectModel model, List<String> collectFilterList, boolean withAlias) {
        if (model.getWhereFilter() == null) {
            return;
        }
        String whereStmt = fixWhereStatement(model, model.getWhereFilter(), withAlias );
        if (whereStmt != null && whereStmt.trim().length() > 0) {
            collectFilterList.add(whereStmt);
        }
    }

    protected void buildFinderStatement(SqlDialectModel model, List<String> collectFilterList, boolean withAlias) {
        if (model.getFinderFields() == null) {
            return;
        }
        for (Field vf : model.getFinderFields()) {
            StringBuilder sb = new StringBuilder();
            if(withAlias) {
                sb.append(getDelimiters()[0] + vf.getTablealias() + getDelimiters()[1] + ".");
            }
            sb.append(getDelimiters()[0] + vf.getFieldname() + getDelimiters()[1]);
            sb.append("=");
            
            if( vf.getSubQuery()!=null) {
                try {
                    sb.append( "(");
                    sb.append( getSelectStatement( vf.getSubQuery() )); 
                    sb.append(")");
                }
                catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                sb.append("$P{" + vf.getExtendedName() + "}");
            }
            collectFilterList.add(sb.toString());
        }
    }

    public String getCreateStatement(SqlDialectModel model) {
        final StringBuilder sb = new StringBuilder();
        final StringBuilder valueBuff = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(getDelimiters()[0] + model.getTablename() + getDelimiters()[1]);
        sb.append(" (");
        int i = 0;
        for (Field fld : model.getFields()) {
            if (!fld.isInsertable()) {
                continue;
            }
            if (i++ > 0) {
                sb.append(",");
                valueBuff.append(",");
            }
            sb.append(getDelimiters()[0] + fld.getFieldname() + getDelimiters()[1]);
            valueBuff.append("$P{" + fld.getExtendedName() + "}");
        }
        sb.append(") VALUES (");
        sb.append(valueBuff);
        sb.append(")");
        return sb.toString();
    }

    /**
     * This is called after inserting the new data.
     */
    public String getUpdateOneToOneForUpdate(SqlDialectModel model) {
        final StringBuilder sb = new StringBuilder();
        final StringBuilder valueBuff = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(getDelimiters()[0] + model.getTablename() + getDelimiters()[1]);
        sb.append(" (");
        int i = 0;
        for (Field fld : model.getFields()) {
            if (i++ > 0) {
                sb.append(",");
                valueBuff.append(",");
            }
            sb.append(getDelimiters()[0] + fld.getFieldname() + getDelimiters()[1]);
            valueBuff.append("$P{" + fld.getName() + "}");
        }
        sb.append(") VALUES (");
        sb.append(valueBuff);
        sb.append(")");
        return sb.toString();

    }

    protected String buildUpdateFieldsStatement(SqlDialectModel model, boolean withAlias) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SET ");
        int i = 0;
        for (Field vf : model.getFields()) {
            if (i++ > 0) {
                sb.append(", ");
            }
            if( withAlias ) {
                sb.append(getDelimiters()[0] + vf.getTablealias() + getDelimiters()[1] + ".");
            }
            sb.append(getDelimiters()[0] + vf.getFieldname() + getDelimiters()[1]);
            sb.append("=");
            
            if( !ValueUtil.isEmpty(vf.getExpr()) ) {
                sb.append( fixStatement(model, vf.getExpr(), withAlias) );
            }
            else {
                sb.append("$P{" + vf.getExtendedName() + "}");
            }
        }
        return sb.toString();
    }

    //***
    // This displays the tables in the following order:
    // maintable, [<link> <linkalias>]* 
    protected String buildListTablesForUpdate(SqlDialectModel model) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (AbstractSchemaView vw : model.getJoinedViews()) {
            if (i++ > 0) {
                sb.append(", ");
            }
            sb.append(getDelimiters()[0] + vw.getTablename() + getDelimiters()[1]);
            if (!vw.getTablename().equals(vw.getName())) {
                sb.append(" ");
                sb.append(" " + getDelimiters()[0]+vw.getName()+getDelimiters()[1] + " ");
            }
        }
        return sb.toString();
    }

    protected void buildJoinTablesForUpdate(SqlDialectModel model, List<String> collectFilterList) {
        if (model.getJoinedViews().size() == 0) {
            return;
        }
        for (AbstractSchemaView avw : model.getJoinedViews()) {
            if (!(avw instanceof LinkedSchemaView)) {
                continue;
            }
            LinkedSchemaView lsv = (LinkedSchemaView) avw;
            for (SchemaViewRelationField rf : lsv.getRelationFields()) {
                StringBuilder sb = new StringBuilder();
                sb.append(getDelimiters()[0] + rf.getTablealias() + getDelimiters()[1] + ".");
                sb.append(getDelimiters()[0] + rf.getFieldname() + getDelimiters()[1]);
                sb.append("=");
                sb.append(getDelimiters()[0] + rf.getTargetView().getName() + getDelimiters()[1] + ".");
                sb.append(getDelimiters()[0] + rf.getTargetField().getFieldname() + getDelimiters()[1]);
                collectFilterList.add(sb.toString());
            }
        }
    }
    
    protected String buildTablesForSelect( SqlDialectModel model ) { 
        return buildTablesForSelect( model, false );  
    }
    
    protected String buildTablesForSelect( SqlDialectModel model, boolean joinTablesOnly ) {
        StringBuilder sb = new StringBuilder(); 
        if ( !joinTablesOnly ) { 
            sb.append(getDelimiters()[0] + model.getTablename() + getDelimiters()[1]);
            if (!model.getTablename().equals(model.getTablealias())) {
                sb.append(" " + getDelimiters()[0] +model.getTablealias() + getDelimiters()[1] + " ");
            }
        }
        if (model.getJoinedViews() != null) {
            int i = 0;
            String sjoinType = " INNER ";
            for (AbstractSchemaView asv : model.getJoinedViews()) {
                if ( !(asv instanceof LinkedSchemaView) ) continue;
                LinkedSchemaView lsv = (LinkedSchemaView)asv; 
                if (!lsv.isRequired()) {
                    //if there is just one instance of not required then immediately make everyhting left join
                    //This is a temporary solution because the ideal solution should have been nested.
                    sjoinType = " LEFT ";
                } 
                sb.append( sjoinType + " JOIN " );
                sb.append(" " + getDelimiters()[0] + lsv.getTablename() + getDelimiters()[1] + " ");
                if (!lsv.getTablename().equals(lsv.getName())) {
                    sb.append(" " + getDelimiters()[0] + lsv.getName() + getDelimiters()[1] + " ");
                }
                sb.append(" ON ");
                int j = 0;
                for (SchemaViewRelationField rf : lsv.getRelationFields()) {
                    if (j++ > 0) {
                        sb.append(" AND ");
                    }
                    sb.append(getDelimiters()[0] + rf.getTablealias() + getDelimiters()[1] + ".");
                    sb.append(getDelimiters()[0] + rf.getFieldname() + getDelimiters()[1]);
                    sb.append("=");
                    sb.append(getDelimiters()[0] + rf.getTargetView().getName() + getDelimiters()[1] + ".");
                    sb.append(getDelimiters()[0] + rf.getTargetField().getFieldname() + getDelimiters()[1]);
                }
            }
        }
        
        //we'll also include the subqueries if any:
        if( model.getSubqueries()!=null && model.getSubqueries().size()>0 ) {
            for( Object m: model.getSubqueries().entrySet() ) {
                Map.Entry<String, SqlDialectModel> me = (Map.Entry)m;
                sb.append( ",");
                sb.append( "( ");
                sb.append( getSelectStatement(me.getValue(), true) );
                sb.append( ") ");
                sb.append( getDelimiters()[0]+me.getKey()+getDelimiters()[1] );
                /*
                sb.append( " ON ");
                int i = 0;
                for(JoinRelationKey rk: me.getValue().getRelationKeys() ) {
                    if( i++>0) sb.append( " AND ");
                    sb.append( getDelimiters()[0] + rk.getSourceField().getTablealias() + getDelimiters()[1] +"." );
                    sb.append( getDelimiters()[0] + rk.getSourceField().getFieldname() + getDelimiters()[1] );
                    sb.append( "=" );
                    sb.append( getDelimiters()[0] + rk.getTargetField().getTablealias() + getDelimiters()[1] +"." );
                    sb.append( getDelimiters()[0] + rk.getTargetField().getFieldname() + getDelimiters()[1] );
                }
                */ 
            }
        }
        
        return sb.toString();
    }

    protected String buildSelectFields(SqlDialectModel model) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for( Field f: model.getFields() ) {
            if(i++>0) sb.append(",");
            if( ValueUtil.isEmpty(f.getExpr()) ) {
                sb.append( getDelimiters()[0]+f.getTablealias()+getDelimiters()[1]+"." );
                sb.append( getDelimiters()[0]+f.getFieldname()+getDelimiters()[1] );
                if(! f.getExtendedName().equals(f.getFieldname()) ) {
                    sb.append( " AS " + getDelimiters()[0]+f.getExtendedName()+getDelimiters()[1] );
                }
            }
            else {
                sb.append( fixStatement(model, f.getExpr(), true) );
                if( !ValueUtil.isEmpty(f.getExtendedName()) ) {
                    sb.append( " AS " + getDelimiters()[0]+f.getExtendedName()+getDelimiters()[1] );
                }
            }
        }
        return sb.toString();
    }
    
    protected String buildGroupByStatement(SqlDialectModel model) {
        StringBuilder sb = new StringBuilder();
        if( model.getGroupFields()!=null &&  model.getGroupFields().size()>0 ) {
            sb.append( " GROUP BY ");
            int i = 0;
            for( Field f: model.getGroupFields() ) {
                if(i++>0) sb.append(",");
                if( ValueUtil.isEmpty(f.getExpr()) ) {
                    sb.append( getDelimiters()[0]+f.getTablealias()+getDelimiters()[1]+"." );
                    sb.append( getDelimiters()[0]+f.getFieldname()+getDelimiters()[1] );
                }
                else {
                    sb.append( fixStatement(model, f.getExpr(), true) );
                }
            }
        }
        return sb.toString();
    }
    
    protected String buildOrderStatement(SqlDialectModel model) {
        return buildOrderStatement( model, null ); 
    }
    protected String buildOrderStatement(SqlDialectModel model, String alias ) { 
        return buildOrderStatement( model, alias, true );
    }
    protected String buildOrderStatement( SqlDialectModel model, String alias, boolean withOrderByCommand  ) { 
        StringBuilder sb = new StringBuilder(); 
        if( model.getOrderFields()!=null &&  model.getOrderFields().size()>0 ) { 
            if ( withOrderByCommand ) sb.append( " ORDER BY "); 
            
            int i = 0;
            for( Field f: model.getOrderFields() ) {
                //if (f.isPrimary()) { continue; } 
                
                if(i++>0) sb.append(",");
                if( ValueUtil.isEmpty(f.getExpr()) ) { 
                    String preferredAlias = f.getTablealias(); 
                    if ( alias != null ) preferredAlias = alias.trim(); 

                    if ( preferredAlias != null && preferredAlias.length()>0 ) {
                        sb.append( getDelimiters()[0]+preferredAlias+getDelimiters()[1]+"." );
                    } 
                    sb.append( getDelimiters()[0]+f.getFieldname()+getDelimiters()[1] );
                } else {
                    sb.append( fixStatement(model, f.getExpr(), true) );
                } 
                sb.append( " " + f.getSortDirection() );
            }
        }
        return sb.toString();
    }
    
    protected String buildWhereForSelect(SqlDialectModel model, WhereFilter wf) {
        StringBuilder sb = new StringBuilder();
        List<String> filters = new ArrayList();
        buildFinderStatement(model, filters,true);
        buildSingleWhereStatement(model, filters,true);
        if( wf!=null) {
            filters.add( fixWhereStatement(model, wf, true ));
        }
        if (filters.size() > 0) {
            sb.append(" WHERE ");
            sb.append(concatFilterStatement(filters));
        }
        return sb.toString();
    }
    
    /**
     * params is applicable for subqueries
     */ 
    public String getSelectStatement(SqlDialectModel model, boolean includeOrderBy) {
        StringBuilder sb = new StringBuilder();
        if( model.getOrWhereList() != null && model.getOrWhereList().size()>1 ) {
            int i = 0;
            StringBuilder buff = new StringBuilder();
            List<Field> pkFields = getPKFields( model ); 
            String pkSelectField = buildSelectPKFields( model, pkFields );  
            
            for( WhereFilter wf: model.getOrWhereList() ) {
                if (i++ > 0) buff.append( " UNION ");

                buff.append(" SELECT ")
                    .append( buildSelectFields( model ))
                    .append(" FROM ")
                    .append( buildTablesForSelect( model ))
                    .append( buildWhereForSelect( model, wf))
                    .append( buildGroupByStatement( model ));
            } 
            
            String baseTableName = (getDelimiters()[0] + model.getTablename() + getDelimiters()[1]); 
            String baseTableAlias = model.getTablealias(); 
            if ( baseTableAlias != null && baseTableAlias.trim().length() > 0 ) {
                baseTableAlias = (getDelimiters()[0] + baseTableAlias + getDelimiters()[1]); 
            } else {
                baseTableAlias = baseTableName; 
            }
            
            sb.append(" SELECT * FROM ( ").append( buff ).append(" )t1 ");
            if ( includeOrderBy ) { 
              sb.append( buildOrderStatement( model, "" )); 
            } 
            
        } else { 
            WhereFilter wf = null; 
            if( model.getOrWhereList() != null && model.getOrWhereList().size()==1 ) {
                wf = model.getOrWhereList().get(0); 
            }
                
            sb.append(" SELECT ");
            sb.append( buildSelectFields(model) );
            sb.append(" FROM ");
            sb.append( buildTablesForSelect(model) );
            sb.append( buildWhereForSelect(model, wf) );
            sb.append( buildGroupByStatement(model) );
            if ( includeOrderBy ) {
                sb.append( buildOrderStatement(model));
            } 
        } 
        return sb.toString();
    }
    
    public String getReadStatement(SqlDialectModel model) throws Exception {
        return getSelectStatement(model);
    }
    
    private List<Field> getPKFields( SqlDialectModel model ) {
        List<Field> targets = new ArrayList();
        List<Field> sources = model.getFields(); 
        for ( Field f : sources ) {
            if ( f.isPrimary() ) {
                targets.add( f ); 
            }
        }
        return targets; 
    }
    private String buildSelectPKFields( SqlDialectModel model, List<Field> fields ) { 
        StringBuilder sb = new StringBuilder(); 
        if ( fields != null && fields.size()>0 ) { 
            for ( int i=0; i<fields.size(); i++ ) { 
                Field f = fields.get(i); 
                if ( i > 0 ) sb.append(", "); 
                
                sb.append( getDelimiters()[0]+f.getTablealias()+getDelimiters()[1]+"." );
                sb.append( getDelimiters()[0]+f.getFieldname()+getDelimiters()[1] );
                sb.append(" AS pk"+ (i+1)); 
            } 
        }
        return sb.toString(); 
    }
    private String buildJoinMatches( List<Field> fields, String sourceAlias, String targetAlias ) { 
        StringBuilder sb = new StringBuilder(); 
        if ( fields != null && fields.size()>0 ) { 
            for ( int i=0; i<fields.size(); i++ ) { 
                Field f = fields.get(i); 
                if ( i > 0 ) sb.append(" AND "); 
                
                sb.append( sourceAlias ).append(".");
                sb.append( getDelimiters()[0]+f.getFieldname()+getDelimiters()[1] );
                sb.append( " = " ).append( targetAlias ).append(".pk"+ (i+1)); 
            } 
        }
        return sb.toString(); 
    }
}
