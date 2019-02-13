/*
 * CheckedParameterBuilder.java
 *
 * Created on February 4, 2013, 9:13 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script;

import com.rameses.annotations.Param;
import com.rameses.classutils.ClassDef;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elmo
 */
public final class CheckedParameterBuilder {
    
    public static final CheckedParameter[] getCheckedParameters(String method, ClassDef classDef ) {
        List<CheckedParameter> params = new ArrayList();
        //must loop
        Method m = classDef.findMethodByName( method );
        int colIndex = 0;
        for(Annotation[] annots: m.getParameterAnnotations()) {
            
            //check if there are annotations
            for(Annotation a: annots) {
                if(a.annotationType() == Param.class) {
                    Param p = (Param)a;
                    String schema = p.value();
                    if(schema.trim().length()==0) schema = p.schema();
                    params.add( new CheckedParameter(schema,colIndex, p.required(), p.types()));
                }
            }
            colIndex++;
        }
        return params.toArray(new CheckedParameter[]{}) ;
    }
    
}
