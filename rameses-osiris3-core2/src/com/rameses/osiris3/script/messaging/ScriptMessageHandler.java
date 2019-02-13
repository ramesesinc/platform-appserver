/*
 * ScriptMessageHandler.java
 *
 * Created on February 6, 2013, 9:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.messaging;

import com.rameses.common.ExpressionResolver;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.TransactionContext;
import com.rameses.osiris3.script.*;
import com.rameses.osiris3.xconnection.MessageHandler;
import com.rameses.util.ExceptionManager;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 * This is used by Messaging
 */
public class ScriptMessageHandler implements MessageHandler {
    
    private MainContext context;
    private String serviceName;
    private String methodName;
    private String evalExpr;
    
    /** Creates a new instance of ScriptMessageHandler */
    public ScriptMessageHandler(MainContext ctx, String serviceName, String methodName, String expr) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.context = ctx;
        this.evalExpr = expr;
    }
    
    //this is overridable
    public IScriptRunnableListener getScriptListener() {
        return null;
    }
    
    public void onMessage(Object data) {
        try {
            ScriptService svc = context.getService( ScriptService.class );
            TransactionContext txn = TransactionContext.getCurrentContext();
            ScriptRunnable sr = new ScriptRunnable(context);
            if( getScriptListener()!=null )sr.setListener(getScriptListener());
            sr.setArgs( new Object[]{data} );
            sr.setEnv( new HashMap() );
            sr.setMethodName( methodName );
            sr.setServiceName( serviceName );
            context.submitAsync( sr );
            //call the service here.
        } catch (Exception ex) {
            ExceptionManager.getOriginal(ex).printStackTrace();
        }
    }

    public int hashCode() {
        return (serviceName + "." + methodName).hashCode();
    }
    
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    public boolean accept(Object data) 
    {
        boolean accept = true;
        if ( evalExpr!=null && evalExpr.trim().length()>0  ) 
        {
            Map map = new HashMap();
            map.put("data", data);
            try {
                accept = ExpressionResolver.getInstance().evalBoolean(evalExpr, map);    
            } catch(Exception ign){;}
        }
        return accept;
    }
    
}
