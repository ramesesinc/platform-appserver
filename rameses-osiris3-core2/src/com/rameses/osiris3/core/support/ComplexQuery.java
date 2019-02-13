/*
 * ComplexQuery.java
 *
 * Created on August 7, 2013, 1:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core.support;

import com.rameses.osiris3.core.TransactionContext;
import groovy.lang.GroovyShell;
import java.util.Map;

/**
 *
 * @author Elmo
 * This
 */
public class ComplexQuery {
    
    
    public Object getResult( String statement, Map params ) throws Exception {
        try {
            ComplexSqlParser parser = new ComplexSqlParser();
            String s = parser.parseStatement( statement );
            GroovyShell shell = new GroovyShell();
            shell.setVariable( "PARAMS", params );
            shell.setVariable( "EM", new EntityManagerProvider() );
            shell.setVariable( "ENV", TransactionContext.getCurrentContext().getEnv() );
            return shell.evaluate( s );
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
}
