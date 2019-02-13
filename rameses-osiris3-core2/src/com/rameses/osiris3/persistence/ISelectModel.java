/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.JoinLink;
import com.rameses.osiris3.persistence.EntityManagerModel.WhereElement;
import com.rameses.osiris3.schema.SchemaView;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
public interface ISelectModel {
    SchemaView getSchemaView();
    String getSelectFields();
    String getGroupByExpr();
    String getOrderExpr();
    int getStart();
    int getLimit();
    WhereElement getWhereElement();
    List<WhereElement> getOrWhereList();
    Map getFinders();
    Map<String, SubQueryModel> getSubqueries();
    List<JoinLink> getJoinLinks();
}
