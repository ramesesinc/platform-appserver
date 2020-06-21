/*
 * MsSqlDialect.java
 *
 * Created on April 30, 2012, 8:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.sql.dialect;



import com.rameses.osiris3.sql.AbstractSqlDialect;
import com.rameses.osiris3.sql.SqlDialectModel;
import java.util.ArrayList;
import java.util.List;



import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Elmo
 * implementing paging routine for mssql server
 * 
 * modified-by: wflores  
 */
public class MsSqlDialect extends AbstractSqlDialect  {
    
    private static final Pattern FN_PATTERN = Pattern.compile("[a-zA-Z]\\w+\\(.*?\\)");
    
    public String getName() {
        return "mssql";
    }
    
    public String[] getDelimiters() {
        return new String[]{"[","]"};
    }
    
    public String getPagingStatement(String sql, int start, int limit, String[] pagingKeys) {
        try {
            return doParse(sql, start, limit, pagingKeys);
        } catch(RuntimeException re) {
            System.out.println("=== error parsing statement ===\n" + sql + "===========");
            throw re; 
        } catch(Exception e) {
            System.out.println("=== error parsing statement ===\n" + sql + "===========");
            throw new RuntimeException(e.getMessage(), e); 
        }
    }

    private String doParse(String sql, int start, int limit, String[] pagingKeys) {
        SQLObject so = new SQLObject(sql, start, limit, pagingKeys);  
        so.parse(); 
        
        int ver = getVersion(); 
        if ( ver > 0 && ver <= 2000 ) {
            return getPagingStatementVer2000( so ); 
        } else {
            return getPagingStatementDefault( so );
        }      
    }
    
    public String getSelectStatement( SqlDialectModel model )  {
        if ( model.getStart()>0 || model.getLimit()>0 ) { 
            return getPagingStatement( model );  
        } else {
             return super.getSelectStatement(model, true);
        }
    }

    public String getUpdateStatement(SqlDialectModel model) {
        final StringBuilder sb = new StringBuilder();
        final StringBuilder whereBuff = new StringBuilder();
        sb.append( " UPDATE ");
        sb.append( resolveTableName( model.getTablealias()) );
        sb.append( " ");
        sb.append( buildUpdateFieldsStatement(model, true) );
        
        sb.append( " FROM " );
        sb.append( buildListTablesForUpdate(model));
        
        sb.append( " WHERE " );
        List<String> list = new ArrayList();
        buildJoinTablesForUpdate(model, list);
        buildFinderStatement(model, list, true);
        buildSingleWhereStatement(model, list, true);
        sb.append( concatFilterStatement(list));     
        return sb.toString();
    }

    public String getDeleteStatement(SqlDialectModel model) {
        final StringBuilder sb = new StringBuilder();
        sb.append( " DELETE FROM ");
        sb.append( resolveTableName( model.getTablename() ));
        sb.append( " WHERE ");
        List<String> list = new ArrayList();
        buildFinderStatement(model, list, false);
        buildSingleWhereStatement(model, list,false);
        sb.append( concatFilterStatement(list));        
        
        return sb.toString();
    }    
    
    protected String getPagingStatement( SqlDialectModel model ) { 
        int ver = getVersion(); 
        if ( ver > 0 && ver <= 2000 ) {
            return getPagingStatementVer2000( model ); 
        } else {
            return getPagingStatementDefault( model );
        }
    }    
    
    private String getPagingStatementDefault( SqlDialectModel model ) { 
        int _start = model.getStart();
        int _limit = model.getLimit();

        StringBuilder buff = new StringBuilder();
        buff.append(" SELECT ");      
        if ( model.getLimit() > 0) { 
            buff.append("TOP "+ (_limit + _start + 1) +" "); 
        } else { 
            buff.append("TOP 1000 "); 
        } 

        if ( model.getOrWhereList() == null || model.getOrWhereList().isEmpty()) {
            buff.append( buildSelectFields( model )).append(" FROM ")
                .append( buildTablesForSelect( model ))
                .append( buildWhereForSelect( model, null))
                .append( buildGroupByStatement( model ))
                .append( buildOrderStatement( model ));

        } else {
            int i = 0;
            StringBuilder union = new StringBuilder();                 
            for( SqlDialectModel.WhereFilter wf : model.getOrWhereList() ) {
                if (i++ > 0) union.append( " UNION ");

                union.append(" SELECT ")
                     .append( buildSelectFields( model ))
                     .append(" FROM ")
                     .append( buildTablesForSelect( model ))
                     .append( buildWhereForSelect( model, wf))
                     .append( buildGroupByStatement( model ));
            } 
            buff.append(" * FROM ( ").append( union ).append(" )t0 ")
                .append( buildOrderStatement( model, "", true, true ));
        } 

        StringBuilder buff2 = new StringBuilder(); 
        buff2.append(" SELECT ROW_NUMBER() OVER (ORDER BY (SELECT 1)) AS _rownum_, t1.* ");
        buff2.append(" FROM ( ").append( buff ).append(" )t1 "); 

        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT "); 
        if ( model.getLimit() > 0 ) {
            sb.append(" TOP " + model.getLimit() ).append(" ");
        }
        sb.append(" t2.* "); 
        sb.append(" FROM ( ").append( buff2 ).append(" )t2  "); 
        sb.append(" WHERE _rownum_ > "+ _start); 
        return sb.toString(); 
    } 
    
    private String getPagingStatementVer2000( SqlDialectModel model ) { 
        int _start = model.getStart(); 
        int _limit = model.getLimit(); 
        
        String pagingKey = getPagingKey( model ); 
        StringBuilder buff = new StringBuilder();
        if ( model.getOrWhereList() == null || model.getOrWhereList().isEmpty()) {
            buff.append( buildSelectFields( model )).append(" FROM ")
                .append( buildTablesForSelect( model ))
                .append( buildWhereForSelect( model, null))
                .append( buildGroupByStatement( model ))
                .append( buildOrderStatement( model ));

        } else {
            int i = 0;
            StringBuilder union = new StringBuilder();                 
            for( SqlDialectModel.WhereFilter wf : model.getOrWhereList() ) {
                if (i++ > 0) union.append( " UNION ");

                union.append(" SELECT ")
                     .append( buildSelectFields( model ))
                     .append(" FROM ")
                     .append( buildTablesForSelect( model ))
                     .append( buildWhereForSelect( model, wf))
                     .append( buildGroupByStatement( model ));
            } 
            buff.append(" * FROM ( ").append( union ).append(" )t0 ")
                .append( buildOrderStatement( model, "" ));
        } 
        
        StringBuilder topbuff = new StringBuilder(); 
        topbuff.append(" SELECT ");      
        if ( model.getLimit() > 0) { 
            topbuff.append("TOP "+ (_limit + _start + 1)).append(" "); 
        } else { 
            topbuff.append("TOP 1000 "); 
        } 
        topbuff.append( buff ); 
        if ( model.getLimit()==1 && model.getStart()==0 ) { 
            return topbuff.toString(); 
        } 
        
        StringBuilder childbuff = new StringBuilder(); 
        childbuff.append(" SELECT TOP "+ _start).append(" "); 
        childbuff.append( buff ); 

        buff = new StringBuilder(); 
        buff.append(" SELECT * FROM ( ").append( topbuff ).append(" )at1 "); 
        buff.append(" WHERE ").append( pagingKey ).append(" NOT IN ( "); 
        buff.append(" SELECT ").append( pagingKey );
        buff.append(" FROM ( ").append( childbuff ).append(" )bt1 "); 
        buff.append(" WHERE ").append( pagingKey ).append("=at1.").append( pagingKey ); 
        buff.append(" ) "); 

        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT "); 
        if ( model.getLimit() > 0 ) {
            sb.append(" TOP " + model.getLimit() ).append(" ");
        }
        sb.append(" * FROM ( ").append( buff ).append(" )xxx  "); 
        return sb.toString(); 
    } 
    
    private String getPagingKey( SqlDialectModel model ) {
        String pagingKey = model.getPagingKeys(); 
        if ( pagingKey == null ) {
            List<SqlDialectModel.Field> sources = model.getFields(); 
            for ( SqlDialectModel.Field f : sources ) {
                if ( f.isPrimary() ) {
                    pagingKey = (getDelimiters()[0]+ f.getFieldname() +getDelimiters()[1]);
                    break; 
                } 
            } 
        } 
        if ( pagingKey == null ) {
            pagingKey = "objid"; 
        } 
        return pagingKey; 
    } 
    
    private String getPagingStatementDefault( SQLObject so ) { 
        StringBuilder buff = new StringBuilder();
        buff.append( so.selectBuilder ).append(" ");
        
        if ( so.hasSelectTop ) { 
            buff.append( so.sqlSelectTop ); 
        } else if ( so.limit > 0 ) { 
            buff.append(" TOP "+ (so.limit + so.start)).append(" ");
        } else { 
            buff.append(" TOP 1000 "); 
        } 
        
        //buff.append(" ROW_NUMBER() OVER (ORDER BY (SELECT 1)) AS _rownum_, ");
        
        if ( so.hasSelectTop ) {
            buff.append( so.sqlSelectCols ); 
        } else {
            buff.append( so.columnBuilder ); 
        }

        buff.append(" ");
        buff.append( so.fromBuilder ); 
        buff.append( so.whereBuilder ); 
        buff.append( so.groupBuilder ); 
        buff.append( so.havingBuilder ); 
        buff.append( so.orderBuilder ); 

        StringBuilder tmpa = new StringBuilder();
        tmpa.append(" SELECT ");
        tmpa.append("    ROW_NUMBER() OVER (ORDER BY (SELECT 1)) AS _rownum_, tmpa.* ");
        tmpa.append(" FROM ( ").append( buff ).append(" )tmpa "); 
        
        StringBuilder tmpb = new StringBuilder();
        tmpb.append(" SELECT ");
        if ( so.limit > 0 ) {
            tmpb.append(" TOP " + so.limit).append(" "); 
        } 
        tmpb.append(" * FROM ( ").append( tmpa ).append(" )tmpb "); 
        if ( so.start >= 0 ) { 
            tmpb.append(" WHERE _rownum_ > "+ so.start ); 
            //sb.append(" ORDER BY _rownum_ "); 
        } 
        return tmpb.toString();  
    } 
    
    private String getPagingStatementVer2000( SQLObject so ) { 
        String[] pkeys = so.pagingKeys; 
        String pagingKey = (pkeys==null || pkeys.length==0 ? null: pkeys[0]); 
        if ( pagingKey == null ) pagingKey = "objid"; 
        
        int idx0 = pagingKey.indexOf('.'); 
        if ( idx0 >= 0 ) {
            pagingKey = pagingKey.substring(idx0+1); 
        }
        
        StringBuilder buff = new StringBuilder();
        buff.append( so.selectBuilder ).append(" ");
        
        if ( so.hasSelectTop ) { 
            buff.append( so.sqlSelectTop ); 
        } else if ( so.limit > 0 ) { 
            buff.append(" TOP "+ (so.limit + so.start)).append(" "); 
        } else { 
            buff.append(" TOP 1000 "); 
        } 
        
        if ( so.hasSelectTop ) {
            buff.append( so.sqlSelectCols ); 
        } else {
            buff.append( so.columnBuilder ); 
        }
        
        buff.append(" ");
        buff.append( so.fromBuilder ); 
        buff.append( so.whereBuilder ); 
        buff.append( so.groupBuilder ); 
        buff.append( so.havingBuilder ); 
        buff.append( so.orderBuilder ); 
        
        StringBuilder at1 = new StringBuilder(); 
        at1.append(" SELECT * FROM ( ").append( buff ).append(" )at1 "); 
        at1.append(" WHERE ").append( pagingKey ).append(" NOT IN ( "); 
        
        buff = new StringBuilder(); 
        buff.append( so.selectBuilder ).append(" TOP "+ so.start ).append(" "); 
        buff.append( so.hasSelectTop ? so.sqlSelectCols : so.columnBuilder ).append(" ");
        buff.append( so.fromBuilder ); 
        buff.append( so.whereBuilder ); 
        buff.append( so.groupBuilder ); 
        buff.append( so.havingBuilder ); 
        buff.append( so.orderBuilder ); 
        StringBuilder bt1 = new StringBuilder(); 
        bt1.append(" SELECT ").append( pagingKey ); 
        bt1.append(" FROM ( ").append( buff ).append(" )bt1 "); 
        bt1.append(" WHERE ").append( pagingKey ).append("=at1.").append( pagingKey ); 
        at1.append( bt1 ).append(" ) "); 
        
        buff = new StringBuilder(); 
        buff.append(" SELECT ");
        if ( so.limit > 0 ) {
            buff.append(" TOP " + so.limit).append(" "); 
        } 
        buff.append(" * FROM ( ").append( at1 ).append(" )xxx ");  
        return buff.toString(); 
    }
    
    
    private class SQLObject {
        
        private String sql; 
        private int start;
        private int limit;
        private String[] pagingKeys;
        
        StringBuilder selectBuilder = new StringBuilder();
        StringBuilder columnBuilder = new StringBuilder();
        StringBuilder fromBuilder = new StringBuilder();
        StringBuilder whereBuilder = new StringBuilder();
        StringBuilder groupBuilder = new StringBuilder();
        StringBuilder havingBuilder = new StringBuilder();
        StringBuilder orderBuilder = new StringBuilder();
        
        boolean hasSelectTop;
        String sqlSelectTop = null; 
        String sqlSelectCols = null; 
        
        SQLObject( String sql, int start, int limit, String[] pagingKeys ) {
            this.sql = sql; 
            this.start = start;
            this.limit = limit;
            this.pagingKeys = pagingKeys; 
        }
        
        void parse() {
            String ids = "objid";
            if( pagingKeys !=null && pagingKeys.length>0) {
                boolean firstTime = true;
                StringBuilder keys = new StringBuilder();
                for( String s: pagingKeys) {
                    if (!firstTime) keys.append("+");
                    else firstTime = false;
                    
                    keys.append( s );
                } 
                ids = keys.toString();
            }

            int STATE_SELECT = 0;
            int STATE_COLUMNS = 1;
            int STATE_FROM = 2;
            int STATE_WHERE = 3;
            int STATE_GROUP = 4;
            int STATE_HAVING = 5;
            int STATE_ORDER = 6;

            selectBuilder = new StringBuilder();
            columnBuilder = new StringBuilder();
            fromBuilder = new StringBuilder();
            whereBuilder = new StringBuilder();
            groupBuilder = new StringBuilder();
            havingBuilder = new StringBuilder();
            orderBuilder = new StringBuilder();

            StringBuilder currentBuilder = null;
            Stack stack = new Stack();
            int currentState = STATE_SELECT;
            boolean hasDistinct = false;

            StringTokenizer st = new StringTokenizer(sql.trim());
            while(st.hasMoreElements()) {
                String s = (String)st.nextElement(); 
                if( s.equalsIgnoreCase("select") && currentState <= STATE_SELECT  ) {
                    selectBuilder.append( s  );
                    currentBuilder = columnBuilder;
                    currentState = STATE_COLUMNS;
                }
                else if( s.equalsIgnoreCase("distinct")) {
                   selectBuilder.append( " DISTINCT " );
                }
                else if( s.equalsIgnoreCase("from") && currentState == STATE_COLUMNS && stack.empty()  ) {
                    currentBuilder = fromBuilder;
                    currentBuilder.append( " " + s );
                    currentState = STATE_FROM;
                } 
                else if( s.equalsIgnoreCase("where") && currentState == STATE_FROM && stack.empty()) {
                    currentBuilder = whereBuilder;
                    currentBuilder.append( " " + s );
                    currentState = STATE_WHERE;
                }
                else if( s.equalsIgnoreCase("group") && currentState <= STATE_WHERE && currentState != STATE_COLUMNS && stack.empty() ) {
                    currentBuilder = groupBuilder;
                    currentBuilder.append( " " + s );
                    currentState = STATE_GROUP;
                }
                else if( s.equalsIgnoreCase("having") && currentState <= STATE_GROUP && currentState != STATE_COLUMNS && stack.empty() ) {
                    currentBuilder = havingBuilder;
                    currentBuilder.append( " " + s );
                    currentState = STATE_HAVING;
                }
                //else if( s.equalsIgnoreCase("order") && currentState <= STATE_HAVING && currentState != STATE_COLUMNS && stack.empty() ) 
                else if( s.equalsIgnoreCase("order") ) {
                    currentBuilder = orderBuilder;
                    currentBuilder.append( " " + s );
                    currentState = STATE_ORDER;
                }
                else if(s.equals("(") || s.trim().startsWith("(") || s.trim().endsWith("(")) {
                    if( currentState != STATE_WHERE ) {
                        stack.push(true);
                    }
                    currentBuilder.append( " " + s );
                }
                else if(s.equals(")") || s.trim().startsWith(")") || s.trim().endsWith(")")) {
                    if( currentState != STATE_WHERE && !FN_PATTERN.matcher(s).matches()  && !stack.isEmpty()) {
                        stack.pop();
                    }
                    currentBuilder.append( " " + s );
                }
                else {
                    currentBuilder.append( " " + s );
                }
            } 

            Pattern p = Pattern.compile("(TOP[\\s]{1,}[0-9]{1,}[\\s]{1,}PERCENT).*?", Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE); 
            Matcher m = p.matcher( columnBuilder );
            if ( m.find() ) {
                sqlSelectTop = m.group();
                sqlSelectCols = columnBuilder.substring( m.end() ); 
                hasSelectTop = true; 

            } else {
                p = Pattern.compile("(TOP[\\s]{1,}[0-9]{1,}[\\s]{1,}).*?", Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE); 
                m = p.matcher( columnBuilder );
                if ( m.find() ) {
                    sqlSelectTop = m.group();
                    sqlSelectCols = columnBuilder.substring( m.end() ); 
                    hasSelectTop = true; 
                } 
            } 
        }    
    } 
}
