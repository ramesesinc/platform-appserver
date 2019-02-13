/*
 * ScriptUtil.java
 *
 * Created on February 6, 2013, 1:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.mgmt;

import com.rameses.osiris3.core.AbstractContext;
import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.data.DataService;

/**
 *
 * @author Elmo
 */
public class SchemaUtil {
    
    public static void clearSchema(AbstractContext ctx, String name) {
        //get also the shared
        if(ctx instanceof MainContext) {
            DataService ds = ((MainContext)ctx).getService( DataService.class );
            ds.clearSchema( name );
        }
    }
    
    public static void clearSchema(AbstractContext ctx) {
       //get also the shared
        if(ctx instanceof MainContext) {
            DataService ds = ((MainContext)ctx).getService( DataService.class );
            ds.clearSchema(null);
        }
    }
    
    public static void clearSql(AbstractContext ctx, String name) {
       //get also the shared
        if(ctx instanceof MainContext) {
            DataService ds = ((MainContext)ctx).getService( DataService.class );
            ds.clearSql(name);
        }
    }
    
    public static void clearSql(AbstractContext ctx) {
       //get also the shared
        if(ctx instanceof MainContext) {
            DataService ds = ((MainContext)ctx).getService( DataService.class );
            ds.clearSql(null);
        }
    }

}
