/*
 * SchemaManagerImpl.java
 *
 * Created on October 16, 2010, 6:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.data;

import com.rameses.osiris3.schema.SchemaConf;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.schema.SchemaSerializer;
import com.rameses.util.ObjectDeserializer;
import com.rameses.util.ObjectSerializer;
import java.io.Serializable;

/**
 *
 * @author ms
 */
public class SchemaManagerImpl extends SchemaManager {
    
    private SchemaConf conf;
    
    public SchemaManagerImpl() {
    }
    
    public void setConf(SchemaConf c) {
        this.conf = c;
        conf.setSerializer(new SchemaMgmtSerializer());
    }
    
    public SchemaConf getConf() {
        return conf;
    }
    
    
    
    
    public class SchemaMgmtSerializer implements SchemaSerializer, Serializable {
        
        public Object read(Object p) {
            if(!(p instanceof String)) return p;
            return read((String)p);
        }
        
        public Object read(String s) {
            return new ObjectDeserializer().read(s);
        }
        
        public String write(Object o) {
            return new ObjectSerializer().toString( o );
        }
    }
    
}
