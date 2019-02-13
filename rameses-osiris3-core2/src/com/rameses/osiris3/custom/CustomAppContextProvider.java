/*
 * CustomAppContextProvider.java
 *
 * Created on January 27, 2013, 5:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.custom;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.ContextProvider;
import com.rameses.osiris3.core.ContextResource;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.util.Service;
import java.util.Iterator;

/**
 *
 * @author Elmo
 */
public class CustomAppContextProvider extends ContextProvider {
    
    private String contextHome;
    
    public CustomAppContextProvider(OsirisServer s) {
        super(s);
    }
    
    protected AbstractContext findContext(String name) {
        return new AppContext(server, name);
    }
    
    protected void initContext(AbstractContext ac) {
       Iterator<ContextResource> res = Service.providers(ContextResource.class, getClass().getClassLoader());
       while(res.hasNext()) {
           ContextResource cs = res.next();
           cs.setServer(server);
           cs.setContext(ac);
           cs.init();
           //System.out.println("resource "+cs);
           ac.addResource( cs.getResourceClass(), cs );
       }

        MainContext mc = (MainContext)ac;
        //for injecting TransactionContext
        mc.setTransactionManagerProvider( new CustomTransactionManagerProvider(server, mc) );
    }
    
    public String getRootUrl() {
        return server.getRootUrl()+"/apps";
    }

    protected String getConfUrl(String name) {
        return getRootUrl()+ "/" + name + "/app.conf";
    }
    
    protected String getClassLoaderPath(String name) {
        return getRootUrl()+ "/" + name +"/modules";
    }


    
    
}
