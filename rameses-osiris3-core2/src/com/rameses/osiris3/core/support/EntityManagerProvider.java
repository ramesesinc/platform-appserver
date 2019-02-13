/*
 * EntityManagerProvider.java
 *
 * Created on August 7, 2013, 1:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core.support;

import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.data.DataService;
import com.rameses.osiris3.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 * This is for getting entity managers via code and not thru injection
 */
public class EntityManagerProvider {
    
    private Map<String,EntityManager> em = new HashMap();
    
    public EntityManager get(String name) {
        if(em.containsKey(name)) {
            return em.get(name);
        } else {
            TransactionContext txn = TransactionContext.getCurrentContext();
            DataService dataSvc = txn.getContext().getService(DataService.class);
            EntityManager q= dataSvc.getEntityManager( name );
            em.put(name, q);
            return q;
        }
    }
    
    
}
