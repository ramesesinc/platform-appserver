/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.sql.SqlExprParserUtil;
import com.rameses.util.ValueUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author dell
 * This will tokenize the select fields, for example 
 * lastname,firstname,address.barangay.objid, a:{ }
 *  
 * This is mainly used for select fields, order field lists, and group fields list
 */
public class SelectFieldsTokenizer {
    
    public static class Token {
        private String text;
        private String expr;
        private String alias;
        private boolean hasExpr;
        private boolean inGroup;
        private boolean inOrder;
        private String sortDirection = "ASC";
        
        private StringBuilder buff = new StringBuilder();
        public void append( String s ) {
            buff.append( s );
        }
        public String toString() {
            return text;
        }
        public void init() {
            text = buff.toString().trim();
            //check first if there is asc or descending. The expression might be used in order by statements
            if( text.toUpperCase().matches(".*ASC")) {
                sortDirection = "ASC";
                int j = text.toUpperCase().lastIndexOf("ASC");
                text = text.substring(0, j ).trim();
            }
            else if( text.toUpperCase().matches(".*DESC")) {
                sortDirection = "DESC";
                int j = text.toUpperCase().lastIndexOf("DESC");
                text = text.substring(0, j).trim();
            }
            hasExpr = text.matches( ".*:\\s{0,}\\{.*\\}\\s{0,}" );
            if( hasExpr) {
                int idx = text.indexOf(":");
                alias = text.substring(0, idx).trim();
                expr = text.substring(idx+1).trim();
                //get the curly braces out
                expr = expr.substring(1, expr.length()-1);
            }
        }
        public String getFieldMatch() {
            return text;
        }        
        public boolean hasExpr() {
            return hasExpr;
        }
        public String getAlias() {
            return alias;
        }
        public String getExpr() {
            return expr;
        }

        public boolean isInGroup() {
            return inGroup;
        }

        public void setInGroup(boolean inGroup) {
            this.inGroup = inGroup;
        }

        public boolean isInOrder() {
            return inOrder;
        }

        public void setInOrder(boolean inOrder) {
            this.inOrder = inOrder;
        }

        public String getSortDirection() {
            return sortDirection;
        }

        public void setSortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
        }
    }
    
    public static List<Token> tokenize( String expr )  {
        List<Token> tokenList = new ArrayList();
        if( ValueUtil.isEmpty(expr) || expr.equals("*") || expr.equals(".*") ) {
            Token t = new Token();
            t.append(".*");
            t.init();
            tokenList.add( t );
            return tokenList;
        }
        expr = expr.trim();
        InputStream is = null;
        Token token = new Token();
        Stack stack = new Stack();
        try {
            is = new ByteArrayInputStream(expr.getBytes());
            StreamTokenizer st = SqlExprParserUtil.createStreamTokenizer(is);
            
            int i = 0;
            while ((i = st.nextToken()) != st.TT_EOF) {
                if (i == st.TT_WORD) {
                    String v = st.sval;
                    token.append( v );
                }
                else if( i == '*') {
                    token.append( (char)i+"" );
                }
                
                else if( i == ',') {
                    if( stack.empty() ) {
                        token.init();
                        tokenList.add(token);
                        token = new Token();
                    }
                    else {
                        token.append(",");
                    }
                }
                else if( i == '{') {
                    stack.push("{");
                    token.append("{");
                }
                else if(i== '}') {
                    stack.pop();
                    token.append("}");
                }
                else if (i == st.TT_NUMBER) {
                    if( i == -2 ) {
                        token.append(".");
                    }
                    else {
                        token.append( st.nval+"" );
                    }
                } 
                else if( i == '\'') {
                    token.append( "'" + st.sval + "'" );
                }
                else {
                    token.append( (char)i+"" );
                }
            }
            if(!stack.empty()) {
                throw new Exception("Expression is not balanced. Please check unbalanced braces");
            }  
            //add the last token in case there is no commas left
            token.init();
            tokenList.add(token);
            return tokenList;
        } 
        catch (Exception ex) {
            //do nothing
            throw new RuntimeException(ex);
        } 
        finally {
            try {
                is.close();
            } catch (Exception e) {;
            }
        }
    }
    
    
}
