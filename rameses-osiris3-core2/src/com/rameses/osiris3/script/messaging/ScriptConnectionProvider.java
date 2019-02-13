/*
 * ScriptConnectionProvider.java
 *
 * Created on February 24, 2013, 3:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.messaging;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.script.*;
import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionProvider;

import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ScriptConnectionProvider extends XConnectionProvider {
    
    public String getProviderName() {
        return "script";
    }

    public XConnection createConnection(String name, Map conf) {
        return new ScriptConnection(name, context, conf);
    }

}