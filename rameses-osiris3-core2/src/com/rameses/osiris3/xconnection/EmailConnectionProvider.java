/*
 * EmailXConnection.java
 *
 * Created on April 25, 2014, 8:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.xconnection;

import java.util.Map;

/**
 *
 * @author Elmo
 */
public class EmailConnectionProvider extends XConnectionProvider {
    
    /** Creates a new instance of EmailXConnection */
    public EmailConnectionProvider() {
    }

    public String getProviderName() {
       return "email";
    }

    public XConnection createConnection(String name, Map conf) {
        return new EmailConnection(name,context, conf);
    }
    
}
