/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.JoinLink;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaView;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 * This is a package level model. This should not be visible from the outside world
 */
public class EntityManagerModel implements ISelectModel {
    
    private SchemaView schemaView;
    private String name;
    private SchemaElement element;
    
    private String selectFields;
    
    private Map finders = new HashMap();
    private Map<String, SubQueryModel> subqueries = new LinkedHashMap();
    private Map vars = new HashMap();
    
    private WhereElement whereElement;
    private String orderExpr;
    private String groupByExpr;
    private List<WhereElement> orWhereList;
    
    private List<JoinLink> joinLinks = new ArrayList();
    
    private int start;
    private int limit;
    private String pagingKeys;
    
    public EntityManagerModel( SchemaElement elem ) {
        element = elem;
        this.name = elem.getName();
        this.schemaView = elem.createView();
    }
    
    public SchemaElement getElement() {
        return element;
    }

    public Map getFinders() {
        return finders;
    }
    
    public void addSubquery(String name, SubQueryModel model ) {
        subqueries.put(name, model);
    }

    public SchemaView getSchemaView() {
        return schemaView;
    }

    public Map getVars() {
        return vars;
    }

    public WhereElement getWhereElement() {
        return whereElement;
    }

    public void setWhereElement(String expr, Map params) {
        if(params==null) params = new HashMap();
        this.whereElement = new WhereElement(expr, params);
    }

    public void addOrWhereElement(String expr, Map params) {
        if(params==null) params = new HashMap();
        if( orWhereList == null ) orWhereList = new ArrayList();
        orWhereList.add( new WhereElement(expr, params) );
    }
    
    public List<WhereElement> getOrWhereList() {
        return orWhereList;
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

    public String getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(String selectFields) {
        this.selectFields = selectFields;
    }


    public void setOrderExpr(String orderExpr) {
        this.orderExpr = orderExpr;
    }

    public String getOrderExpr() {
        return orderExpr;
    }

    public String getGroupByExpr() {
        return groupByExpr;
    }

    public void setGroupByExpr(String groupByExpr) {
        this.groupByExpr = groupByExpr;
    }


    public static class WhereElement {
        private String expr;
        private Map params;

        public WhereElement(String expr, Map params) {
            this.expr = expr;
            this.params = params;
            if(this.params==null) this.params = new HashMap();
        }
        public String getExpr() {
            return expr;
        }

        public Map getParams() {
            return params;
        }
    }
    
    public static class OrderElement {
        private String field;
        private String direction;

        public OrderElement(String f, String d) {
            this.field = f;
            this.direction = d;
        }
        
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }
     
    public Map<String,SubQueryModel> getSubqueries() {
        return this.subqueries;
    }
     
    public Map getWhereParams() {
        Map map = new HashMap();
        if( whereElement == null ) return map;
        map.putAll( whereElement.getParams() );
        /*
        for( WhereElement we: this.whereList ) {
            map.putAll( we.getParams() );
        }
        */ 
        return map;
    } 
     
    //call this if you want to check if the finders or the where expr is specfied
    public boolean hasCriteria() {
        if( getFinders()!=null && getFinders().size() >0  ) return true;
        if( getWhereElement()!=null && getWhereElement().getExpr()!=null 
                && getWhereElement().getExpr().trim().length()>0 ) return true;
        return false;
    }
    
    public void addJoinLink( JoinLink j ) {
        this.joinLinks.add( j );
    }
    
    public List<JoinLink> getJoinLinks() {
        return joinLinks;
    }
    
    public String getPagingKeys() { return pagingKeys; } 
    public void setPagingKeys( String pagingKeys ) {
        this.pagingKeys = pagingKeys; 
    }
}
