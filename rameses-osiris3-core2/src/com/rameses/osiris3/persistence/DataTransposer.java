/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.SchemaView;
import com.rameses.osiris3.schema.SchemaViewField;
import com.rameses.osiris3.schema.SchemaViewRelationField;
import com.rameses.util.EntityUtil;
import com.rameses.util.ObjectSerializer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author dell
 * This utility will handle map transformations to prepare it for saving.
 */
public class DataTransposer {
    
    
    /**
     * This results a new map that contains the flattened data. This is for easier 
     * data entry in the insert. It also converts the necessary fields to its proper 
     * data types readay for persisting.
     * @param svw
     * @param sourceData
     * @param targetData 
     */
    public static Map prepareDataForInsert(SchemaView svw, Map sourceData ) throws Exception {
        Map targetData = new LinkedHashMap();
        for( SchemaViewField vf: svw.findAllFields(".*")) {
            if( !vf.getView().equals(svw) ) {
                if(!vf.getView().isExtendedView()) continue;
            }
            //System.out.println(vf.getName()+" "+vf.getView().getName()+"->"+vf.getExtendedName());
            
            //if(!vf.isBaseField()) continue;
            if(! (vf instanceof SchemaViewRelationField )) {
                if(!vf.isInsertable()) continue;
                 //create should only be on the base field
                Object val = EntityUtil.getNestedValue(sourceData, vf.getExtendedName() );
                //ignore if null.
                if(val==null) continue;
                if( vf.isSerialized() && val !=null) {
                    //get the default serializer
                    String ser = (String)vf.getProperty("serializer");
                    if(ser==null) ser = "default";
                    val = ObjectSerializer.getInstance().toString(val);
                }
                targetData.put( vf.getExtendedName(), val);
            }
            else  {
                //do not replace if there is already a value entered. 
                //This is to protect one to many relationships being cascaded
                //do not include one to many
                SchemaViewRelationField svr = (SchemaViewRelationField)vf;
                Object val = EntityUtil.getNestedValue(sourceData, svr.getExtendedName());
                if( val == null ) {
                    //this is usually for many to one.
                    val = EntityUtil.getNestedValue(sourceData, svr.getTargetFieldExtendedName()  );
                    targetData.put( svr.getFieldname(), val);
                }
                else {
                    targetData.put( svr.getExtendedName(), val);
                }
            }
        }
        return targetData;
    }
    
    
    
    
    //This will create a new map of flattened, not nested data. This will also
    //only update fields that exist in the data source
    public static Map prepareDataForUpdate(SchemaView svw, Map rawData ) throws Exception {
        
        Map flattenedData = flatten(rawData, "_");
        
        Map targetData = new LinkedHashMap();
        for( SchemaViewField vf: svw.findAllFields(".*")) {
            if(!vf.isUpdatable()) continue;
            if(! (vf instanceof SchemaViewRelationField )) {
                if( vf.isSerialized() ) {
                    Object val = null;
                    try {
                        val = EntityUtil.getNestedValue(rawData, vf.getExtendedName() );
                        if(val==null) continue;
                        
                        //get the default serializer                        
                        String ser = (String)vf.getProperty("serializer");
                        if(ser==null) ser = "default";
                        val = ObjectSerializer.getInstance().toString(val);
                        targetData.put( vf.getExtendedName(), val);
                    } catch(Throwable ign){;}
                    
                } else if( flattenedData.containsKey(vf.getExtendedName()) ) {
                    Object val = flattenedData.get(vf.getExtendedName());  
                    targetData.put( vf.getExtendedName(), val);
                    
                } else {
                    String fldname = vf.getExtendedName();
                    String[] names = fldname.split("_"); 
                    if ( names.length > 1 && flattenedData.containsKey(names[0]) ) { 
                        targetData.put( fldname, null ); 
                    }
                } 
            }
            else  {
                SchemaViewRelationField svr = (SchemaViewRelationField)vf;
                if( svr.getTargetJoinType().matches( JoinTypes.MANY_TO_ONE +"|"+JoinTypes.ONE_TO_ONE ) ) {
                    if( flattenedData.containsKey(svr.getTargetFieldExtendedName()) ) {
                        Object val = flattenedData.get(svr.getTargetFieldExtendedName());
                        targetData.put( svr.getFieldname(), val);
                    }
                    //check if we need to include this in update. Two fields we need to check
                    //the field directly linked or the embedded object to be linked. 
                    else if ( flattenedData.containsKey(svr.getExtendedName())) {
                        Object val = flattenedData.get( svr.getExtendedName() );
                        targetData.put( svr.getFieldname(), val);
                    }
                }
            }
        }
        return targetData;
    }
    
    
    public final static Map flatten( Map data, String ch) {
        Map newHashMap = new HashMap();
        scanFlatten( data, null, newHashMap, ch  );
        return newHashMap;
    }
    
    private static void scanFlatten( Map data, String prefix, Map result, String ch ) {
        if(ch==null) ch = ".";
        for( Object o: data.entrySet() ) {
            Map.Entry me = (Map.Entry)o;
            Object val = me.getValue();
            String keyName = ((prefix==null) ? "" : prefix+ch) + me.getKey();
            if( !(val instanceof Map )) {
                result.put(keyName, val);
            }
            else {
                scanFlatten( (Map)val, keyName, result, ch );
            }
        }
    }
    
    private static class _Helper {
        String join( String[] values, String delim, int startIndex, int endIndex ) { 
            int _limit = values.length; 
            String _delim = (delim == null? "" : delim);
            StringBuilder sb = new StringBuilder(); 
            for (int i=startIndex, len=endIndex+1; i<len; i++) {
                if ( i >= _limit ) { break; }
                
                if ( values[i] == null ) {
                    //do nothing 
                } else {
                    if ( sb.length() > 0 ) { 
                        sb.append( _delim ); 
                    } 
                    sb.append( values[i] );
                }
            }
            return sb.toString(); 
        }
    }
}
