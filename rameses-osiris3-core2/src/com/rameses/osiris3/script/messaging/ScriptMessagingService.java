/*
 * ScriptMessagingService.java
 *
 * Created on February 25, 2013, 10:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.messaging;

import com.rameses.annotations.OnEvent;
import com.rameses.annotations.OnMessage;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.ContextService;
import com.rameses.osiris3.core.SharedContext;


import com.rameses.osiris3.script.ScriptInfo;
import com.rameses.osiris3.script.ScriptService;
import com.rameses.osiris3.xconnection.MessageConnection;
import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionFactory;
import com.rameses.util.URLDirectory;
import com.rameses.util.URLDirectory.URLFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Elmo
 */
public class ScriptMessagingService extends ContextService {
    
    public final int getRunLevel() {
        return 30;
    }
    
    public Class getProviderClass() {
        return ScriptMessagingService.class;
    }
    
    private void fetchResults(Enumeration<URL> e, final Set<String> list) {
        if(e==null) return;
        while(e.hasMoreElements()) {
            final URL parent = e.nextElement();
            URLDirectory dir = new URLDirectory(parent);
            dir.list( new URLFilter() {
                public boolean accept(URL u, String filter) {
                    list.add( "messaging/"+ filter.substring(filter.lastIndexOf("/")+1)  );
                    return false;
                }
            });
        }
    }
    
    public final void start() throws Exception {
        final Set<String> list = new HashSet();
        
        //scan from shared first
        if(context instanceof AppContext) {
            SharedContext sharedCtx = ((AppContext)context).getSharedContext();
            if(sharedCtx!=null) {
                fetchResults( sharedCtx.getClassLoader().getResources("scripts/messaging"), list );
            }
        }
        
        fetchResults( context.getClassLoader().getResources("scripts/messaging"), list );
        
        //execute each script
        ScriptService svc = context.getService( ScriptService.class );
        for(String sname: list) {
            try {

                ScriptInfo sinfo = svc.findScriptInfo( sname );
                
                //check for OnMessage annotated methods and add basic script message handler
                for( Method m: sinfo.getClassDef().findAnnotatedMethods(OnMessage.class) ) {
                    try {
                        OnMessage om = m.getAnnotation(OnMessage.class);
                        String connName = om.value();
                        if (connName==null || connName.trim().length()==0) { 
                            connName = "default-messaging";
                        }

                        XConnection xconn = context.getResource( XConnection.class, connName );
                        if (xconn instanceof XConnectionFactory) {
                            XConnectionFactory factory = (XConnectionFactory) xconn;
                            String category = factory.extractCategory(connName); 
                            if (category==null || category.length()==0) {
                                xconn = factory.getConnection(om); 
                            } else {
                                xconn = factory.getConnection(category); 
                            } 
                        }
                        
                        MessageConnection mconn = (MessageConnection) xconn;
                        if(mconn != null) {
                            mconn.addHandler( new ScriptMessageHandler(context, sname, m.getName(), om.eval()) );
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                        System.out.println(sname +"."+ m.getName() +": failed to load handler caused by "+ e.getMessage());
                    }
                }

                //check for OnEvent annotated methods and add script event handlers
                for( Method m: sinfo.getClassDef().findAnnotatedMethods(OnEvent.class) ) {
                    try {
                        OnEvent oe = m.getAnnotation(OnEvent.class);
                        String connName = oe.value();

                        if( connName ==null || connName.trim().length()==0) connName = "default-messaging";
                        MessageConnection conn = (MessageConnection) context.getResource( XConnection.class, connName );
                        if(conn!=null) {
                            conn.addHandler( new ScriptEventMessageHandler(context, sname, m.getName(), oe.eval(), oe.pattern() ) );
                        }
                    } catch(Exception e) {
                        System.out.println("error unable to load script event handler " + sname + "."+m.getName() + " cause:"+e.getMessage());
                    }
                }
            } catch(Exception e) {
                System.out.println("ERROR LOADING MESSAGING SCRIPT. "+ e.getMessage());
                e.printStackTrace();
            }     
        }
    }
    
    public final void stop() throws Exception {
        //
    }
    
}
