/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.sql.dialect.functions.mysql;

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
        String length = params.get(1);
        String prefix = params.get(2); 
        String suffix = params.get(3); 
        sb.append("CASE WHEN ").append(""+length).append(" > 0 THEN ");
        sb.append("CONCAT(IFNULL(").append(prefix).append(",''),");
        sb.append("CONVERT(LPAD(").append( seriesno ).append(",").append(""+length).append(",'0'),char(255)),");
        sb.append("IFNULL(").append(suffix).append(",'')) "); 
        sb.append(" ELSE ");
        sb.append("CONCAT(IFNULL(").append(prefix).append(",''),"); 
        sb.append( seriesno ).append(",IFNULL(").append(suffix).append(",'')) "); 
        sb.append(" END "); 
        return sb.toString(); 
    } 
}
