/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mssql;

import java.util.ArrayList;
import java.util.List;

/*
 * SYNTAX: FORMAT_SERIES( seriesno, length, prefix, suffix )
 */
public class FORMAT_SERIES { 
    
    protected List<String> params = new ArrayList(); 
    
    public String getName() {
        return "FORMAT_SERIES";
    }

    public void addParam(String s) {
        params.add( s ); 
    }

    public String toString() { 
        StringBuilder sb = new StringBuilder(); 
        String seriesno = params.get(0); 
        String serieslen = params.get(1);
        String prefix = params.get(2); 
        String suffix = params.get(3); 
        sb.append("CASE WHEN "+ serieslen +" > 0 THEN ");
        sb.append("(ISNULL("+ prefix +",'') + ");
        sb.append("RIGHT(REPLICATE('0', "+ serieslen +") + LEFT(CONVERT(CHAR(50),"+ seriesno +"), "+ serieslen +"), "+ serieslen +") + ");
        sb.append("ISNULL("+ suffix +",'')) "); 
        sb.append(" ELSE ");
        sb.append("(ISNULL("+ prefix +",'') + CONVERT(CHAR(50),"+ seriesno +") + ISNULL("+ suffix +",'')) "); 
        sb.append(" END "); 
        return sb.toString(); 
    } 
}
