/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.RelationKey;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaRelation;
import com.rameses.osiris3.schema.SchemaUtil;
import com.rameses.osiris3.schema.SimpleField;
import com.rameses.util.EntityUtil;
import java.rmi.server.UID;
import java.util.List;
import java.util.Map;

/**
 * @author dell
 * This utility fills the data prior to saving..
 */
public class DataFillUtil {
    
    /**
     * Fills the raw data with default values like id's and dates
     * and other default values in sequence
     * base fields, serialized fields, extende d fields, one to one, one to many
     * 
     * @param svw
     * @param data 
     */
    private static UIDKeyGenerator uidGenerator = new UIDKeyGenerator();
     
    private static String generateId( SimpleField sf  ) throws Exception {
        String keyGen = (String) sf.getProperty("keygen");
        String prefix = (String) sf.getProperty("prefix");
        
        //if there is a key generator specified in keygen use that instead.
        if( keyGen == null || keyGen.trim().length()==0 || keyGen.equalsIgnoreCase("default") ) {
            //use the basic keygen
            return uidGenerator.getNewKey(prefix, 0);
        }
        return ((prefix==null)?"":prefix)+new UID();
    }
    
    public static void convertData( SimpleField sf, Map data, Object val ) throws Exception {
        if(val!=null) {
            String stype = sf.getType();
            if(stype==null) stype = "string";
            val = SchemaUtil.convertData( val, stype );
        }
        //correct also the data type
        EntityUtil.putNestedValue( data, sf.getName(), val );
    }
    
    public static void fillInitialData( SchemaElement elem, Map rawData ) throws Exception {
        for( SimpleField sf: elem.getSimpleFields() ) {
            //insert primary key ids.
            Object val = EntityUtil.getNestedValue(rawData, sf.getName());
            if( val == null ) {
                if( sf.isPrimary() ) {
                    //fill only if base field.
                    rawData.put( sf.getName(), generateId(sf) );
                }
                else if( sf.getDefaultValue()!=null ) {
                    rawData.put( sf.getName(), sf.getDefaultValue() );
                }
            }
        }
        
        //fill in the extende d items
        if( elem.getExtendedElement()!=null ) {
            fillInitialData( elem.getExtendedElement(), rawData );
        }
        
        //for one to one, specify the objid 
        for(SchemaRelation sr: elem.getOneToOneRelationships()) {
            Map m = (Map)rawData.get(sr.getName());
            if(m!=null) {
                SchemaElement tgt = sr.getLinkedElement();                
                fillInitialData( tgt, m );
            }
        }
        fillOneToManyData( elem, rawData );
    }
    
    private static void fillOneToManyData(SchemaElement elem, Map rawData) throws Exception {
        for(SchemaRelation sr: elem.getOneToManyRelationships()) {
            List list = (List)rawData.get(sr.getName());
            if(list!=null) {
                for(  Object o : list) {
                    if(o instanceof Map) {
                        Map e = (Map)o;
                        //ordinary load items. 
                        for( RelationKey rk: sr.getRelationKeys() ) {
                            Object val = EntityUtil.getNestedValue(rawData, rk.getField() );
                            if( val !=null) {
                                e.put( rk.getTarget(), val  );
                            }
                        }
                        fillInitialData( sr.getLinkedElement(), e );
                    }
                }
            }
        }
    }
    
    
}
