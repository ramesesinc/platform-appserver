/*
 * ActiveDBManager.java
 *
 * Created on August 30, 2013, 1:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.activedb;

import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.TransactionManager;
import com.rameses.osiris3.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ActiveDBTransactionManager implements TransactionManager {
   
    private MainContext mainCtx;
    private Map<String, ActiveDBInvoker> invokers = new HashMap();
    
    public ActiveDBTransactionManager(MainContext ctx) {
        mainCtx = ctx;
    }
    
    //returns a proxy method
    public Object create(String schemaName, EntityManager em) {
        ActiveDBInvoker adi = new ActiveDBInvoker(schemaName, em);
        ActiveDBService ads = mainCtx.getService( ActiveDBService.class );
        return ads.create( adi );
    }
    
    public void commit() {
    }

    public void rollback() {
    }

    public void close() {
        //this is where we will put close and return to pool.
        //System.out.println("closing active db context now");
        invokers.clear();
        invokers = null;
    }
    
}
