/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.schema;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author dell
 */
public interface SchemaViewFieldFilter {
    
    boolean accept( SchemaViewField vw );
    
    public static  abstract class AbstractViewFilter implements SchemaViewFieldFilter {
        
        protected Set<String> duplicates = new HashSet();
        protected boolean includeDuplicates = false;
        protected String matchPattern;
        
        protected String correctMatchPattern(String fieldNames) {
            String arr[] = fieldNames.split(",");
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for(String s: arr) {
                if(i++ > 0 ) sb.append("|");
                sb.append(s.trim());
            }
            return sb.toString();        
        } 
        
        protected boolean hasNoDuplicates(String n) {
            boolean added = duplicates.add(n);
            if(!added) return false;
            return true;  
        }
    }
    
    /**
     * View that filters based on the extended field name. 
     */
    public static class ExtendedNameViewFieldFilter extends AbstractViewFilter {
        public ExtendedNameViewFieldFilter(String s) {
            this(s, false);
        }
        public ExtendedNameViewFieldFilter(String s, boolean includeDuplicates) {
            this.matchPattern = correctMatchPattern(s);
            this.includeDuplicates = includeDuplicates;
        }
        public boolean accept(SchemaViewField vw) {
            boolean pass = vw.getExtendedName().matches(matchPattern);
            if(!pass) return false;
            if( includeDuplicates ) return true;
            return hasNoDuplicates( vw.getExtendedName() );
        }
    }
    
    /**
     * View that filters based on the element name. 
     */
    public static class ElementNameViewFieldFilter extends AbstractViewFilter {

        public ElementNameViewFieldFilter(String s) {
            this(s, false);
        }
        public ElementNameViewFieldFilter(String s, boolean includeDuplicates) {
            this.matchPattern = correctMatchPattern(s);
            this.includeDuplicates = includeDuplicates;
        }
        public boolean accept(SchemaViewField vw) {
            boolean pass = vw.getElement().getName().matches(matchPattern);
            if(!pass) return false;
            if( includeDuplicates ) return true;
            return hasNoDuplicates( vw.getExtendedName() );
        }
    }
    
}
