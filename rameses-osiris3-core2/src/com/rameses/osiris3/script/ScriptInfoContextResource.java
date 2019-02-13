/*
 * ScriptInfoContextResource.java
 *
 * Created on January 27, 2013, 8:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.osiris3.core.*;
import groovy.lang.GroovyClassLoader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ScriptInfoContextResource extends ContextResource {
    
    public void init() {
        //do nothing
    }
    
    public Class getResourceClass() {
        return ScriptInfo.class;
    }
    
    public ScriptInfo parseScript(String name, InputStream is, URL u, AbstractContext ctx ) throws Exception {
        try {
            GroovyClassLoader gc = (GroovyClassLoader)ctx.getClassLoader();
            Class clazz = gc.parseClass( is );
            return new ScriptInfo(name, u, clazz,  ctx, gc);
        } catch(Exception e) {
            System.out.println("[ScriptInfo] failed to parse script " + name);
            e.printStackTrace();
            throw e;
        }
    }
    
    public ScriptInfo findResource(String name) {
        InputStream is = null;
        try {
            //we use context classloader bec. we need this to be dynamic
            URL u = context.getClassLoader().getResource( "scripts/"+name );
            if (u == null) throw new ResourceNotFoundException("File " + name + " not found" );
            is = u.openStream();
            return parseScript(name, is, u, context);
            
        } catch(ResourceNotFoundException rnfe) {
            throw rnfe;
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try { is.close(); } catch(Exception ign){;}
        }
    }
    
    public void remove(String key) {
        ScriptInfo si = (ScriptInfo)resources.remove(key);
        if(si!=null) si.destroy();
    }
    
    public void removeAll() {
        Map forRemoval = new HashMap();
        forRemoval.putAll( resources );
        resources.clear();
        for(Object o: forRemoval.values()) {
            ScriptInfo si = (ScriptInfo)o;
            si.destroy();
        }
        forRemoval.clear();
    }

    
    
}
