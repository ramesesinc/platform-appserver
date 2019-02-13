/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.JoinLink;
import com.rameses.osiris3.persistence.EntityManagerModel.WhereElement;
import com.rameses.osiris3.schema.RelationKey;
import com.rameses.osiris3.schema.SchemaView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
public class SubQueryModel implements ISelectModel {
    
    private EntityManagerModel model;
    private List<RelationKey> relationKeys = new ArrayList();
    private String jointype = "INNER";
    private String name;
    private List<JoinLink> joinLinks = new ArrayList();
    
    public SubQueryModel(EntityManagerModel m) {
        this.model = m;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public EntityManagerModel getModel() {
        return model;
    }

    /**
     * Specify the relationship you want to use for linking. 
     * @param sourceField - refers to this field.
     * @param targetField - the linked field
     */
    public void addRelation( String sourceField, String targetField  ) {
        RelationKey rk = new RelationKey();
        rk.setField(sourceField);
        rk.setTarget(targetField);
        relationKeys.add(rk);
    }

    public List<RelationKey> getRelationKeys() {
        return relationKeys;
    }

    public SchemaView getSchemaView() {
        return model.getSchemaView();
    }

    public String getSelectFields() {
        return model.getSelectFields();
    }

    public String getGroupByExpr() {
        return model.getGroupByExpr();
    }

    public String getOrderExpr() {
        return model.getOrderExpr();
    }

    public int getStart() {
        return model.getStart();
    }

    public int getLimit() {
        return model.getLimit();
    }

    public WhereElement getWhereElement() {
        return model.getWhereElement();
    }

    public List<WhereElement> getOrWhereList() {
        return model.getOrWhereList();
    }

    public Map getFinders() {
        return model.getFinders();
    }

    public Map<String, SubQueryModel> getSubqueries() {
        return model.getSubqueries();
    }

    public String getJointype() {
        return jointype;
    }

    public void setJointype(String jointype) {
        this.jointype = jointype;
    }

    public String getName() {
        return name;
    }


    public List<JoinLink> getJoinLinks() {
        return joinLinks;
    }
    
    
}
