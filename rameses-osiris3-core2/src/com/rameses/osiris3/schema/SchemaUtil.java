/*
 * SchemaTypeUtil.java
 *
 * Created on August 14, 2010, 5:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elmo
 */
public final class SchemaUtil {
    
    //if it returns true, then the required rule passes.
    public static boolean checkRequired( SchemaField f, Object value ) {
        if(!f.isRequired()) return true;
        if( value==null ) {
            return false;
        } else if( value instanceof String ) {
            if( ((String)value).trim().length()==0 ) {
                return false;
            }
        }
        return true;
    }
    
    public static Class getFieldClass(SimpleField f, Object testValue) {
        String type = f.getType();
        if(type!=null && type.trim().length()>0) {
            if( type.equalsIgnoreCase(SimpleFieldTypes.STRING) ) {
                return String.class;
            } else if( type.equalsIgnoreCase(SimpleFieldTypes.TIMESTAMP) ) {
                return Timestamp.class;
            } else if( type.equalsIgnoreCase(SimpleFieldTypes.DATE) ) {
                return Date.class;
            } else if( type.equalsIgnoreCase(SimpleFieldTypes.DECIMAL) ) {
                return BigDecimal.class;
            } else if( type.equalsIgnoreCase(SimpleFieldTypes.DOUBLE) ) {
                return Double.class;
            } else if( type.equalsIgnoreCase(SimpleFieldTypes.INTEGER) ) {
                return Integer.class;
            } else if( type.equalsIgnoreCase(SimpleFieldTypes.BOOLEAN) ) {
                return Integer.class;
            } else if( type.equalsIgnoreCase(SimpleFieldTypes.LONG) ) {
                return Long.class;
            }
        }
        if(testValue !=null)
            return testValue.getClass();
        else
            return Object.class;
    }
    
    
    //if this function returns true, the class type against the field is matched.
    public static boolean checkType( SimpleField f, Class clazz ) {
        String type = f.getType();
        if(type==null || type.trim().length()==0) return true;
        
        Boolean pass = null;
        if( type.equalsIgnoreCase(SimpleFieldTypes.STRING) ) {
            pass = (clazz == String.class);
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.TIMESTAMP) ) {
            pass = (clazz==Timestamp.class);
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.DATE) ) {
            pass = (clazz==Date.class ) || (clazz==java.sql.Date.class);
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.DECIMAL) ) {
            pass = (clazz==BigDecimal.class);
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.DOUBLE) ) {
            pass = (clazz==Double.class) || (clazz==double.class);
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.INTEGER) ) {
            pass = (clazz==Integer.class) || (clazz==int.class);
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.BOOLEAN) ) {
            pass = (clazz==Boolean.class) || (clazz==boolean.class);
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.LONG) ) {
            pass = (clazz==Long.class) || (clazz==long.class);
        }
        if(pass==null) {
            return false;
        } else if(!pass) {
            return false;
        }
        return true;
    }
    
    public static void checkComplexType( ComplexField cf, Object value ) throws Exception {
        String type =cf.getType();
        if( type ==null) return;
        String refName = cf.getName();
        if( type.equalsIgnoreCase("list")) {
            if(! (value instanceof List || value.getClass().isArray() )) 
                throw new Exception("Complex field "+refName + " must be of type List or Array");
        }
        else {
            if(!(value instanceof Map)) {
                throw new Exception("Complex field "+refName + " must be of type Map");
            }
        }
    }
    
    //sends a value, like defaults and transforms it to the expected type
    public static Object formatType( SimpleField f, Object value ) {
        if( value == null ) return null;
        if((value instanceof String) && ((String)value).trim().length()==0 ) return value;
        
        String type = f.getType();
        if(type==null || type.trim().length()==0) return value;
        
        if( type.equalsIgnoreCase(SimpleFieldTypes.STRING) ) {
            return value.toString();
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.TIMESTAMP) ) {
            return Timestamp.valueOf( value.toString() );
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.DATE) ) {
            return java.sql.Date.valueOf( value.toString() );
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.DECIMAL) ) {
            return new BigDecimal(value.toString());
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.DOUBLE) ) {
            return Double.valueOf(value.toString());
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.INTEGER) ) {
            return Integer.valueOf(value.toString());
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.BOOLEAN) ) {
            return Boolean.valueOf(value.toString());
        } else if( type.equalsIgnoreCase(SimpleFieldTypes.LONG) ) {
            return Long.valueOf( value.toString() );
        }
        return null;
    }
    
    
    
    public static boolean checkFieldExist( Map data, String name ) {
        if( name.indexOf("_")>0) {
            //try first if there is an actual field with underscores.
            boolean b = data.containsKey(name); 
            if( b ) return true;
            
            Map odata = data;
            String[] arr = name.split("_");
            for(int i=0; i<(arr.length-1); i++) {
                Object z = odata.get(arr[i]);
                if( z ==null ) continue;
                if( !(z instanceof Map )) {
                    return false;
                }
                odata = (Map)z;
            }
            return odata.containsKey(arr[arr.length-1]);
        }
        else {
            return data.containsKey(name);
        }
    }
    
  
    public static Object convertData( Object v, String stype ) throws Exception {
        Object val = v;
        if( stype!=null && stype.equalsIgnoreCase("integer") ) {
            if(! (val instanceof Integer) ) {
                val = Integer.parseInt(val.toString());
            } 
        }
        else if( stype!=null && stype.equalsIgnoreCase("decimal")) {
            if(! (val instanceof BigDecimal) ) {
                val = new BigDecimal(val.toString());
            } 
        }
        else if( stype!=null && stype.equalsIgnoreCase("date")) {
            if( val instanceof Timestamp ) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String s = sdf.format( (Timestamp)val );
                return sdf.parse(s);
            }
            else if(! (val instanceof Date) ) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                val = sdf.parse(val.toString());
            }
            
        }
        else if( stype!=null && stype.equalsIgnoreCase("boolean")) {
            if(! (val instanceof Boolean) ) {
                val = Boolean.valueOf(val.toString());
            }
        }
        else if( stype!=null && stype.equalsIgnoreCase("timestamp")) {
            if(!(val instanceof Timestamp)) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date dt = sdf.parse(val.toString());
                    val = java.sql.Timestamp.valueOf(sdf.format(dt));
                }
                catch(Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return val;
    }
    

    //this interface is used in conjunction with findNestedField which listens to the elements retrieved
    public static interface SchemaFieldListener {
        void handleLinkedElement( SchemaElement parent, SchemaElement child, String joinType, List<RelationKey> relationKeys );
        void handleExtendedElement( SchemaElement parent, SchemaElement child );
    }
    
    /**
     * element paths is a collection of SchemaElement and RelationKeys
     */
    public static SchemaField findNestedField( SchemaElement baseElement, String key, SchemaFieldListener listener ) throws Exception {
        if( key.contains(".") ) {
            String[] arr = key.split("\\.");
            SchemaElement elem = baseElement;
            for( int i=0; i<arr.length-1;i++ ) {
                SchemaField f = elem.getField(arr[i]);
                if( f instanceof ComplexField ) {
                    ComplexField cf = (ComplexField)f;
                    if( cf.getRef() ==null ) {
                        throw new Exception("SchemaUtil.findNestedField error. Complex field " + arr[i] + " does not have ref. key is " + key);
                    }
                    //replace the element so that it can loop again;
                    SchemaElement parentElem = elem;
                    elem = baseElement.getSchema().getElement(cf.getRef());
                    if( listener!=null) {
                        listener.handleLinkedElement(parentElem, elem, cf.getJoinType(), cf.getRelationKeys() );
                    }
                    continue;
                }
                else {
                    throw new Exception( "SchemaUtil.findNestedField error. field name " + arr[i] + " not a complex field. source is " + key);
                }
            }
            return elem.getField(arr[arr.length-1]);
        }
        else {
            //if field not found, attempt to search all extend elements.
            SchemaField sf =  baseElement.getField(key);
            if( sf == null ) {
                if( baseElement.getExtends()!=null ) {
                    SchemaElement parentElem = baseElement;
                    SchemaElement el = baseElement.getSchema().getSchemaManager().getElement(baseElement.getExtends());
                    while( true ) {
                        sf = el.getField(key);
                        if( listener!=null) {
                            listener.handleExtendedElement(parentElem, el );
                        }
                        if( sf==null && el.getExtends()!=null ) {
                            parentElem = el;
                            el = el.getSchema().getSchemaManager().getElement(el.getExtends());
                        }
                        else {
                            break;
                        }
                    }
                }
            }
            return sf;
        }
    }
    
    
}
