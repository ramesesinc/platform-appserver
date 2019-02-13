/*
 * IRelationalField.java
 *
 * Created on April 29, 2013, 4:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import java.util.List;

/**
 *
 * @author Elmo
 */
public interface IRelationalField {
   List<RelationKey> getRelationKeys();
   void addKey(RelationKey rk);
   String getTarget();
}
