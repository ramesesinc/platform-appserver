/*
 * ComplexSqlParser.java
 *
 * Created on August 7, 2013, 12:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.core.support;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Elmo
 * This is a complex sql parser that handles complex statement like as follows:
 *
 * @[ select * from sys_org WHERE objid=$P{PARAMS.objid} ].each { x->
 *    @[ select * from sys_user_member where userid=$P{x.objid} ]
 * }
 *
 */
public final class ComplexSqlParser {
    
    private static Pattern mainPattern = Pattern.compile("@.*?\\[.*?\\]");
    private static Pattern paramPattern = Pattern.compile("\\$P.?\\{.*?\\}");
    
    public String parseStatement(String d) throws Exception {
        return parseStatement(d,"main");
    }
    
    public String parseStatement(String d, String defaultContext) throws Exception {
        if(d.indexOf("@")>=0) {
            Matcher m = mainPattern.matcher( d );
            int start = 0;
            StringBuilder sb = new StringBuilder();
            
            while(m.find()) {
                int end = m.start();
                sb.append( d.substring(start, end)  );
                sb.append( parseElement(m.group(), defaultContext) );
                start = m.end();
            }
            if( start < d.length() ) sb.append(d.substring(start));
            return sb.toString();
        } else {
            return parseFinal( d, defaultContext, null );
        }
    }
    
    private String parseElement( String s, String defaultContext ) {
        String context = defaultContext;
        String qualifier = null;
        String statement = null;
        String s1 = s.trim().substring(1);
        if(!s1.startsWith("[")) {
            int starter = s1.indexOf("[")+1;
            statement = s1.substring( starter, s1.indexOf("]") );
            s1 = s1.substring(0, starter-1);
            
            //if it starts with ( then its a function
            int qstart = s1.indexOf("(");
            if(qstart == 0) {
                qualifier = s1.substring(qstart+1, s1.indexOf(")"));
            }
            else {
                context = s1;
                if(qstart > 0 ) {
                    context = s1.substring(0, s1.indexOf("("));
                    qualifier = s1.substring(qstart+1, s1.indexOf(")") );
                }
            }
        } else {
            statement = s.trim().substring( 2, s.trim().length()-1  ) ;
        }
        //System.out.println("statement ->"+ statement + " context->"+context + " qualifier->"+qualifier);
        return parseFinal(statement,context,null);
    }
    
    private String parseFinal(String statement, String context, Map qualifiers) {
        //check parameters in statement and store as map params
        StringBuilder paramString = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        Matcher m = paramPattern.matcher( statement );
        int start = 0;
        int i = 0;
        while(m.find()) {
            int end = m.start();
            sb.append( statement.substring(start, end)  );
            String ss = m.group().trim();
            String pValue = ss.substring( ss.indexOf("{")+1, ss.indexOf("}")  );
            String pName = pValue.replace(".","_");
            paramString.append( pName + ":" + pValue );
            if(i++>0) sb.append(",");
            sb.append("$P{"+ pName + "}");
            start = m.end();
        }
        if( start < statement.length() ) sb.append(statement.substring(start));
        statement = sb.toString();
        
        StringBuilder fbuilder = new StringBuilder();
        fbuilder.append("EM[\""+context+"\"].sqlContext.createQuery('''\n" + statement + "\n''')" );
        if( paramString.length()>0 ) fbuilder.append( ".setParameters(["+ paramString.toString()+"])" );
        fbuilder.append( ".dataList" );
        //System.out.println("final ->"+fbuilder.toString());
        return fbuilder.toString();
    }
    
}
