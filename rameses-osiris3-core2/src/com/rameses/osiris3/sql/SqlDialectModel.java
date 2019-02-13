/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.sql;

import com.rameses.osiris3.schema.AbstractSchemaView;
import com.rameses.util.ValueUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



/**
 *
 * @author dell
 * This is used for the 
 */
public class SqlDialectModel {
    
    private String tablename;
    private String tablealias;
    private String action;
    
    private String selectExpression;
    private List<Field> fields = new ArrayList();
    private List<Field> finderFields;
    
    private List<Field> orderFields;
    private List<Field> groupFields;
    
    //fieldMap is a helper field
    private Map<String, Field> fieldMap = new HashMap();
    private Map<String, SqlDialectModel> subqueries = new LinkedHashMap();
    private Map<String, AbstractSchemaView> joinedViewMap = new HashMap(); 
    private AbstractSchemaView schemaView;
    private List<AbstractSchemaView> joinedViews;
    
    //internal fields for finding things
    //for updating and saving
    private int start;
    private int limit;
    
    //consider removing
    
    //private Set<AbstractSchemaView> linkedViews;
    //private LinkedSchemaView linkedView;
    
    private WhereFilter whereFilter;
    private List<WhereFilter> orWhereList;
    
    //marked as true if the link type is inverse. applicable to subqueries 
    private boolean inverseJoin = false;
    
    private String pagingKeys;
    
    /*
     * The id should be unique per call because it will cache the sql units.
     */
    
     public String toString() {
        StringBuilder sb = new StringBuilder(tablealias+":"+tablename+":"+action+";");
        int i = 0;
        if(!ValueUtil.isEmpty(this.selectExpression)) {
            sb.append("select:"+this.selectExpression+";");
        }
        if(fields!=null && fields.size()>0) {
            i = 0;
            sb.append("fields:");
            for( Field f: this.getFields() ) {
                if(i++>0) sb.append(",");
                sb.append( f.getExtendedName() );
                if(! ValueUtil.isEmpty(f.getExpr()) ) {
                    sb.append( "expr:"+f.getExpr());
                } 
            }
            sb.append(";");
        }
        if( finderFields!=null && finderFields.size() > 0 ) {
            sb.append("finders:");
            i = 0;
            for( Field vf : finderFields ) {
                if( i++>0 ) sb.append(",");
                sb.append( vf.getExtendedName() );
            }
            sb.append(";");
        }
        if( getJoinedViews()!=null && getJoinedViews().size()>0 ) {
            sb.append("joinedviews:");
            i = 0;
            for( AbstractSchemaView vw : getJoinedViews() ) {
                if( i++>0 ) sb.append(",");
                sb.append( vw.getName()+":"+vw.getTablename() );
            }
            sb.append(";");
        }
        if( subqueries!=null && subqueries.size()>0 ) {
            sb.append("subqueries:");
            for( SqlDialectModel sqm: subqueries.values() ) {
                sb.append( sqm.toString() );
                sb.append(",");
            }
        }
        if( whereFilter !=null && !ValueUtil.isEmpty(whereFilter.getExpr()) ) {
            sb.append("where:");
            sb.append( whereFilter.getExpr() );
            sb.append(";");
        }
        if( orWhereList!=null && orWhereList.size()>0) {
            sb.append( "orwhere:");
            i = 0;
            for( WhereFilter wf: orWhereList ) {
                if(i++>0) sb.append(",");
                sb.append( wf.getExpr() );
                sb.append(";");
            }        
        }
        if( this.groupFields!=null && this.groupFields.size()>0 ) {
            sb.append( "groupby:");
            i = 0;
            for( Field f: this.getGroupFields() ) {
                if(i++>0) sb.append(",");
                sb.append( f.getExtendedName() );
                if(! ValueUtil.isEmpty(f.getExpr()) ) {
                    sb.append( "expr:"+f.getExpr());
                } 
            }        
            sb.append(";");
        }
        if( this.orderFields!=null && this.orderFields.size()>0 )  {
            sb.append( "orderby:");
            i = 0;
            for( Field f: this.getOrderFields() ) {
                if(i++>0) sb.append(",");
                sb.append( f.getExtendedName() );
                if(! ValueUtil.isEmpty(f.getExpr()) ) {
                    sb.append( "expr:"+f.getExpr());
                } 
                sb.append( "::"+ f.getSortDirection() );
            }        
            sb.append(";");
        }
        if ( start > 0 ) sb.append("start:true;");
        if ( limit > 0 ) sb.append("limit:true;");
        
        if ( action != null && action.equalsIgnoreCase("select")) { 
            sb.append("_sqlkey_:").append( getGenkey()).append(";"); 
        } 
        return sb.toString(); 
    } 
    
    private String _genkey_; 
    private String getGenkey() { 
        if ( _genkey_ == null ) { 
            _genkey_ = new java.rmi.server.UID().toString(); 
        } 
        return _genkey_; 
    }
     
    public int getId() { 
        return toString().hashCode(); 
    } 
     
    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getTablealias() {
        return tablealias;
    }

    public void setTablealias(String tablealias) {
        this.tablealias = tablealias;
    }
    
    public void setFieldMap(Map<String, Field> fieldMap) {
        this.fieldMap = fieldMap;
    }
    
    public Field findField(String name) {
        return fieldMap.get(name);
    }
    
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public List<Field> getFinderFields() {
        return finderFields;
    }

    public void setFinderFields(List<Field> finderFields) {
        this.finderFields = finderFields;
        for( Field vf: finderFields ) {
            fieldMap.put(vf.getExtendedName(), vf);
        }
    }

    public void addSubQuery( String name, SqlDialectModel subQuery ) {
        subqueries.put(name, subQuery);
    }
    
    public Map<String, SqlDialectModel> getSubqueries() {
        return subqueries;
    }
    
    public WhereFilter getWhereFilter() {
        return whereFilter;
    }

    public void setWhereFilter(WhereFilter wf) {
        //this should also add whatever fields are there in the where filter
        this.whereFilter = wf;
    }
    
    public void addOrWhereFilter(WhereFilter wf) {
        if( orWhereList == null ) orWhereList = new ArrayList();
        orWhereList.add(wf);
    }
    
    public List<WhereFilter> getOrWhereList() {
        return orWhereList;
    }

    public String getSelectExpression() {
        return selectExpression;
    }

    public void setSelectExpression(String selectExpression) {
        this.selectExpression = selectExpression;
    }

    public void addField(Field vf) {
        if( !this.fields.contains(vf)) {
            this.fields.add(vf);
        }
    }
    
    public List<Field> getFields() {
        return fields;
    }
    
    public Field getSelectField(String name) {
        for(Field f:this.fields) {
            if( f.getExtendedName().equals(name)) return f;
        }
        return null;
    }
    
    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<Field> getGroupFields() {
        return groupFields;
    }

    public void setGroupFields(List<Field> groupFields) {
        this.groupFields = groupFields;
    }

    public List<Field> getOrderFields() {
        return orderFields;
    }

    public void setOrderFields(List<Field> orderFields) {
        this.orderFields = orderFields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public static class WhereFilter {
        
        private String expr;
        
        public WhereFilter(String expr) {
            this.expr = expr;
        }
        public String getExpr() {
            return expr;
        }
    }

    /*
    public void setJoinedViews(  List<AbstractSchemaView> vws ) {
        this.joinedViews = vws;
    }
    */ 
    
    public static class Field {
        
        private String name;
        private String tablename;
        private String tablealias;
        private String extendedName;
        private String fieldname;
        private boolean primary;
        private boolean insertable;
        private boolean updatable;
        private boolean serialized;
        private boolean basefield;
        private String sortDirection;
        private SqlDialectModel subQuery;
        
        private String expr;
        
        public String getTablename() {
            return tablename;
        }

        public void setTablename(String tablename) {
            this.tablename = tablename;
        }

        public String getTablealias() {
            return tablealias;
        }

        public void setTablealias(String tablealias) {
            this.tablealias = tablealias;
        }

        public String getFieldname() {
            return fieldname;
        }

        public void setFieldname(String fieldname) {
            this.fieldname = fieldname;
        }

        public String getExtendedName() {
            return extendedName;
        }

        public void setExtendedName(String extendedName) {
            this.extendedName = extendedName;
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

        public boolean isInsertable() {
            return insertable;
        }

        public void setInsertable(boolean insertable) {
            this.insertable = insertable;
        }

        public boolean isUpdatable() {
            return updatable;
        }

        public void setUpdatable(boolean updatable) {
            this.updatable = updatable;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSerialized() {
            return serialized;
        }

        public void setSerialized(boolean serialized) {
            this.serialized = serialized;
        }

        public boolean isBasefield() {
            return basefield;
        }

        public void setBasefield(boolean basefield) {
            this.basefield = basefield;
        }

        
        public String getExpr() {
            return expr;
        }

        public void setExpr(String expr) {
            this.expr = expr;
        }

        public String getSortDirection() {
            return sortDirection;
        }

        public void setSortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
        }
        
        public int hashCode() {
            if( this.extendedName == null && this.expr!=null ) {
                return this.expr.hashCode();
            }
            return extendedName.hashCode();
        }

        public boolean equals(Object obj) {
            return hashCode() == obj.hashCode();
        }

        public SqlDialectModel getSubQuery() {
            return subQuery;
        }

        public void setSubQuery(SqlDialectModel subQuery) {
            this.subQuery = subQuery;
        }
    }
    
    //aditional method
    public void addSelectField(Field f) {
        if(!fields.contains(f)) {
            fields.add(f);
        }
    }
    
    public void addExprField(Field f) {
        if( !fieldMap.containsKey(f.getExtendedName()) ) {
            fieldMap.put(f.getExtendedName(), f);
        }
    }
    
    public void addOrderField(Field f) {
        if( orderFields == null ) orderFields = new ArrayList();
        if(!orderFields.contains(f)) {
            orderFields.add(f);
        }
    }
    
    public void addGroupField(Field f) {
        if( groupFields == null ) groupFields = new ArrayList();
        if(!groupFields.contains(f)) {
            groupFields.add(f);
        }
    }

    public void addFinderField(Field f) {
        if(finderFields==null) finderFields = new ArrayList();
        if( !finderFields.contains(f) ) {
            finderFields.add(f);
        }
    }
    
    public void addJoinedViews( List<AbstractSchemaView> views) {
        for( AbstractSchemaView vw: views) {
            addJoinedView( vw );
        }
    }
    
    public void addJoinedView( AbstractSchemaView vw) {
        if( !joinedViewMap.containsKey(vw.getName()) ) {
            joinedViewMap.put(vw.getName(), vw);
        }
    }
    
    public AbstractSchemaView findJoinedView( String name ) {
        return joinedViewMap.get(name);
    }
    
    public List<AbstractSchemaView> getJoinedViews() {
        if( joinedViews == null ) {
            joinedViews = new ArrayList(joinedViewMap.values());
            Collections.sort(joinedViews);
        }
        return joinedViews;
    }

    /*****************************************
     * additional methods to support subquery
     * ***************************************/
    private String joinType;
    
    public String getJoinType() {
        return joinType;
    }
    public void setJoinType(String s) {
        this.joinType = s;
    }
    
    private List<JoinRelationKey> relationKeys = new ArrayList();

    public List<JoinRelationKey> getRelationKeys() {
        return relationKeys;
    }

    public void addRelationKey(Field sourceField, Field targetField ) {
        relationKeys.add( new JoinRelationKey(sourceField, targetField) );
    }
    
    public static class JoinRelationKey {
        private Field sourceField;
        private Field targetField;
        
        public JoinRelationKey(Field source, Field target) {
            this.sourceField = source;
            this.targetField = target;
        }

        public Field getSourceField() {
            return sourceField;
        }

        public Field getTargetField() {
            return targetField;
        }
    }
    
    /***
     *  This creates a new field from the sub queries.
     */ 
    public boolean isInverseJoin() {
        return inverseJoin;
    }
    
    public void setInverseJoin(boolean b) {
        this.inverseJoin = b;
    }
    
    /*
     public SqlDialectModel.Field findFirstSubQueryFields( String matchName ) {
         List<SqlDialectModel.Field> list = findAllSubQueryFields(matchName);
         if( list.size() == 0 ) throw new RuntimeException("Error findFirstSubQueryFields. No fields found in subquery that matches " + matchName);
         return list.iterator().next();
     }
     
     public List<SqlDialectModel.Field> findAllSubQueryFields( String matchName ) {
        List<SqlDialectModel.Field> list = new ArrayList();
        for(Object o: getSubqueries().entrySet()) {
            Map.Entry<String, SqlDialectModel> me = (Map.Entry)o;
            for(Field f: me.getValue().getFields()) {
                String n = me.getKey()+"."+f.getExtendedName();
                if( n.matches(matchName) ) {
                    Field newfld = new Field();
                    newfld.setExtendedName(f.getExtendedName());
                    newfld.setFieldname(f.getExtendedName());
                    newfld.setTablealias(me.getKey());
                    newfld.setTablename(me.getKey());
                    list.add( newfld );
                }
            }
        }
        return list;
    }
    */
    
    public List<SqlDialectModel.Field> findAllSubQueryFields( String prefix, String matchName ) {
        List<SqlDialectModel.Field> list = new ArrayList();
        SqlDialectModel sqd = getSubqueries().get(prefix);
        if( sqd == null ) return null;
        for(Field f: sqd.getFields()) {
            if( f.getExtendedName().matches(matchName) ) {
                Field newfld = new Field();
                newfld.setExtendedName(f.getExtendedName());
                newfld.setFieldname(f.getExtendedName());
                newfld.setTablealias(prefix);
                newfld.setTablename(prefix);
                list.add( newfld );
            }
        }
        return list;
    }
    
    public String getPagingKeys() { return pagingKeys; } 
    public void setPagingKeys( String pagingKeys ) { 
        this.pagingKeys = pagingKeys; 
    } 
}
