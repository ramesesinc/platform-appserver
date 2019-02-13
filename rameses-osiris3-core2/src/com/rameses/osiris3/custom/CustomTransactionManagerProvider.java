/*
 * CustomTransactionManagerProvider.java
 *
 * Created on January 30, 2013, 4:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.custom;

import com.rameses.osiris3.activedb.ActiveDBTransactionManager;
import com.rameses.osiris3.data.ConnectionTransactionManager;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.core.TransactionManager;
import com.rameses.osiris3.core.TransactionManagerProvider;
import com.rameses.osiris3.script.ScriptTransactionManager;

/**
 *
 * @author Elmo
 */
public class CustomTransactionManagerProvider implements TransactionManagerProvider {
    
    private OsirisServer server;
    private MainContext context;
    
    /** Creates a new instance of CustomTransactionManagerProvider */
    public CustomTransactionManagerProvider(OsirisServer s, MainContext c) {
        this.server = s;
        this.context = c;
    }

    public TransactionManager[] getManagers() {
        return new TransactionManager[]{
            new ScriptTransactionManager(context),
            new ConnectionTransactionManager(server),
            new ActiveDBTransactionManager(context)
        };
    }
    
}
