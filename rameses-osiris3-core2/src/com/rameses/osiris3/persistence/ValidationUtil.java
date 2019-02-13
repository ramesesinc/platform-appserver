/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.ComplexField;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaField;
import com.rameses.osiris3.schema.SchemaUtil;
import com.rameses.osiris3.schema.SimpleField;
import com.rameses.util.EntityUtil;
import java.util.List;
import java.util.Map;

/**
 * @author dell
 */
public class ValidationUtil {
    
    public static ValidationResult validate( Object data, SchemaElement elem, String includeFields, String excludeFields) throws Exception { 
        if( data instanceof List ) {
            return validate( (List)data, elem, includeFields, excludeFields );
        }
        else if( data instanceof Map ) {
            return validate( (Map)data, elem, includeFields, excludeFields );
        }
        else {
            throw new RuntimeException( "Validation error. Data miust be List or Map" );
        }
    }
    
    public static ValidationResult validate( List data, SchemaElement elem, String includeFields, String excludeFields) throws Exception { 
        ValidationResult vr = new ValidationResult();
        List list = (List)data;
        int i = 0;
        for(Object d: list) {
            ValidationResult vr2 = validate( (Map)d, elem, includeFields, excludeFields );
            vr2.setContextName(elem.getName()+"["+i+"]");
            if(vr2.hasErrors()) {
                vr.addSubValidation(vr2);
            }
            i++;
        }
        return vr;
    }
    
    public static ValidationResult validate( Map data, SchemaElement element ) throws Exception {
        return validate( data, element, null, null );
    }
    
    public static ValidationResult validate( Map data, SchemaElement element, String includeFields, String excludeFields ) throws Exception {
        ValidationResult vr = new ValidationResult();
        vr.setContextName(element.getName());
        //validate the basic simple and complex fields.
        for(SchemaField fld: element.getFields()) {
            if( includeFields !=null && !fld.getName().matches(includeFields)) continue;
            if( excludeFields !=null && fld.getName().matches(excludeFields)) continue;
            
            String refName = fld.getName();
            Object value = EntityUtil.getNestedValue(data, refName);
            boolean passRequired = SchemaUtil.checkRequired(fld,value);
            if( !passRequired ) {
                vr.addError("", refName + " is required ");
                continue;
            }
            //do not proceed if valus is null bec. it will be useless
            if( value == null ) continue;
            
            if( fld instanceof SimpleField ) {
                //check first if we need to process this field. refer to parentLink stack above
                SimpleField sf = (SimpleField)fld;
                String type = sf.getType();
                //before we check the type we attempt to convert first
                Object oval = SchemaUtil.convertData(value, type);
                Class clazz = oval.getClass();
                boolean passType = SchemaUtil.checkType( sf, clazz );
                if(!passType) {
                    vr.addError( "", refName + " must be of type " + type );
                    continue;
                }
                EntityUtil.putNestedValue(data, refName, oval);
            } 
            else if( fld instanceof ComplexField ) {
                ComplexField cf = (ComplexField)fld;
                String ref = cf.getRef();
                String type = cf.getType();
                String joinType = cf.getJoinType();
                
                //do not include many-to-one because it is not a part of this object. so we do not validate it
                if( joinType !=null && joinType.matches(JoinTypes.MANY_TO_ONE) ) continue;
                
                //check complex type
                if( cf.getSerializer()!=null ) {
                    try {
                        SchemaUtil.checkComplexType( cf, value );
                        //do not validate anymore if ref is not specified
                        if( ref == null ) continue;
                    }        
                    catch(Exception ex) {
                        vr.addError("", ex.getMessage());
                        continue;
                    }
                }
                if( ref == null )
                    throw new RuntimeException("Ref is required for complex type in validation");
                
                 /*
                //add dynamic ref. Dynamic ref are marked with $ and enclosed with braces e.g.:  ${ref-name}
                if( data!=null && ref!=null && ref.indexOf("$")>=0) {
                    ref = ExprUtil.substituteValues(ref, (Map)data );
                }
                if(ref!=null && ref.indexOf("$")<0) {
                    if(ref.indexOf(":")>0) {
                        refElement = schema.getSchemaManager().getElement(ref);
                    } else {
                        refElement = schema.getElement(ref);
                    }
                    if(refName==null) refName = refElement.getName();
                }
                */ 
                SchemaElement refElement = element.getSchema().getSchemaManager().getElement(ref);
                if( refElement == null )
                    throw new RuntimeException("Ref element " + ref + " not found");
                
                ValidationResult vr2 = validate( value, refElement, includeFields, excludeFields);
                if(vr2.hasErrors()) {
                    vr.addSubValidation(vr2);
                }
            }
        }     
        
        //validate the extends if any
        if( element.getExtends()!=null ) {
            SchemaElement ext = element.getSchema().getSchemaManager().getElement(element.getExtends());
            ValidationResult vr2 = validate( (Map)data, ext, includeFields, excludeFields );
            if( vr2.hasErrors()) {
                vr.merge(vr2);
            }
        }
        
        return vr;
    }
    
}
