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
import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.ContextResource;
import com.rameses.osiris3.core.SharedContext;
import com.rameses.osiris3.script.InterceptorSet;
import com.rameses.osiris3.script.ScriptInfo;

/**
 *
 * @author Elmo
 */
public class ScriptUtil {
    
    public static void clearScript(AbstractContext ctx, String name) {
        //get also the shared
        if(ctx instanceof AppContext) {
            SharedContext sctx  = ((AppContext)ctx).getSharedContext();
            if(sctx !=null) {
                sctx.getContextResource( ScriptInfo.class ).remove(name);
            }
        }
        ContextResource rs = ctx.getContextResource( ScriptInfo.class );
        if(rs!=null) rs.remove( name );
    }
    
    public static void clearScript(AbstractContext ctx) {
        //get also the shared
        if(ctx instanceof AppContext) {
            SharedContext sctx= ((AppContext)ctx).getSharedContext();
            if(sctx !=null) {
                sctx.getContextResource( ScriptInfo.class ).removeAll();
            }
        }
        
        ContextResource rs = ctx.getContextResource( ScriptInfo.class );
        if(rs!=null) rs.removeAll();
    }
    
    public static void clearInterceptors(AbstractContext ctx) {
        //get also the shared
        if(ctx instanceof AppContext) {
            SharedContext sctx  = ((AppContext)ctx).getSharedContext();
            if(sctx !=null) {
                sctx.getContextResource( InterceptorSet.class ).removeAll();
            }
        }
        
        ContextResource rs = ctx.getContextResource( InterceptorSet.class );
        if(rs!=null) rs.removeAll();
    }
    
}
