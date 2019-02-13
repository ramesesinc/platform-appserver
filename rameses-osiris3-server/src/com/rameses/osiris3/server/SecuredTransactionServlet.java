/*
 * SecuredTransactionServlet.java
 *
 * Created on January 10, 2013, 4:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.server;

import com.rameses.util.CipherUtil;
import java.io.Serializable;

/**
 *
 * @author Elmo
 */
public class SecuredTransactionServlet extends ServiceInvokerServlet {
    
    protected Object[] filterInput( Object obj ) throws Exception {
        return (Object[]) CipherUtil.decode( (Serializable)obj );
    }

    protected Object filterOutput(Object obj) throws Exception {
        return CipherUtil.encode( (Serializable)obj );
    }
}
