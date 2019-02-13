/*
 * MailMessageConnectionProvider.java
 *
 * Created on February 7, 2013, 4:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.messaging.mail;

import com.rameses.osiris3.xconnection.XConnection;
import com.rameses.osiris3.xconnection.XConnectionProvider;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class MailMessageConnectionProvider extends XConnectionProvider {
    
    public String getProviderName() {
        return "mail";
    }

    public XConnection createConnection(String name, Map conf) {
        return new MailMessageConnection(name,  conf);
    }
    
}
