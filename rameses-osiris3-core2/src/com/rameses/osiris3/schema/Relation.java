/*
 * Relation.java
 *
 * Created on July 26, 2014, 7:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elmo
 */
public class Relation implements IRelationalField {
    
    private String name;
    private String source;
    private String target;
    private String jointype;
    private List<RelationKey> keys = new ArrayList();
    
    /** Creates a new instance of Relation */
    public Relation(String src) {
        this.source = src;
    }
    
    public String getName() {
        if(name==null) name = getSource() + "_" + getTarget();
        return name;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }
    
    public void addKey( RelationKey rk ) {
        this.keys.add( rk );
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getJointype() {
        return jointype;
    }

    public void setJointype(String jointype) {
        this.jointype = jointype;
    }

   

    public List<RelationKey> getRelationKeys() {
        return keys;
    }
    
}
