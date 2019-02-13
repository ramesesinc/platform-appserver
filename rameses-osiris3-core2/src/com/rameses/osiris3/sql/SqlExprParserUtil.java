/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.sql;

import com.rameses.osiris3.sql.SqlDialectModel.Field;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.util.Stack;

/**
 *
 * @author dell
 * Expression parsing must be done in two parts. The first part just filters and corrects the statement
 * The second part is for the sql translator
 */
public class SqlExprParserUtil {
    
    /**
     * Translate should be applicable to any sql expression statements like field expressions, where expressions
     * and subqueries
     * @param sqlModel
     * @param expr
     * @param stmt
     * @return
     * @throws Exception 
     */
    public static String translate( SqlDialectModel sqlModel, String expr, SqlDialect stmt, boolean includeFieldAlias ) throws Exception {
        Stack<ParseContext> stack = new Stack();
        stack.push(new StringBufferParseContext());
        
        //The first regular expression replaces all spaces that are 2 or more to one
        //The second replaces all spaces 1 or more if it precedes comma, open parens and close parens
        //The third replaces all spaces 1 or more if it succeeds comma, open parens and close parens
        
        InputStream is = new ByteArrayInputStream(expr.getBytes());
        StreamTokenizer st = createStreamTokenizer(is);
        
        int i = 0;
        while ((i = st.nextToken()) != st.TT_EOF) {
            ParseContext ctx = stack.peek();
            if (i == st.TT_WORD) {
                String v = st.sval;
                
                //correct first if there are periods to underscores
                if( v.indexOf(".") > 0 ) v = v.replace(".", "_");
                
                //check if field exists. if not ignore it.
                Field vf = sqlModel.findField(v);
                //System.out.println("find field " + v + ((vf!=null)? vf.getExtendedName(): "not found"));
                if(vf!=null) {
                    if(includeFieldAlias) {
                        ctx.append( stmt.getDelimiters()[0]+ vf.getTablealias() +stmt.getDelimiters()[1] +"." );
                    }
                    ctx.append( stmt.getDelimiters()[0]+ vf.getFieldname() +stmt.getDelimiters()[1] );
                    continue;
                }
                
                //check next if the field is found at the subquery
                if( st.sval.indexOf(".")>0 ) {
                    String fname = st.sval;
                    String prefix = fname.substring(0, fname.indexOf("."));
                    fname = fname.substring(fname.indexOf(".")+1).replace(".", "_");
                    SqlDialectModel sqm = sqlModel.getSubqueries().get(prefix);
                    if(sqm!=null) {
                        Field f = sqm.getSelectField(fname);
                        if( f !=null ) {
                            ctx.append( stmt.getDelimiters()[0]+ prefix +stmt.getDelimiters()[1] +"." );
                            ctx.append( stmt.getDelimiters()[0]+ f.getExtendedName() +stmt.getDelimiters()[1]  );
                            continue;
                        }
                    }
                }
                
                //check if next token is open parens then this is a function.
                if(  st.nextToken() == '(') {
                    //check if there is function.
                    SqlDialectFunction func = stmt.getFunction(v);
                    stack.push( new FunctionContext(func) );
                }
                else {
                    st.pushBack();
                    ctx.append(v);
                }
            }
            else if (i == st.TT_NUMBER) {
                ctx.append( st.nval+"" );
            } 
            else if( i == ':') {
                int j = st.nextToken();
                if( j == ':') {
                    //if double semicolon, e.g. :: this should be handled as a var. 
                    //we need to search first if the name exists in the subquery of xmodel. if it exists,
                    //then we should insert it.
                    st.nextToken();
                    ctx.append( "${" + st.sval + "}" );
                }
                else {
                    ctx.append( "$P{" +st.sval + "}" );
                }
            }
            else if( i == '\'') {
                ctx.append( "'" + st.sval + "'" );
            }
            else if( i == '[') {
                //if wrapped with [ we must handle it literally
                st.nextToken();
                ctx.append(  stmt.getDelimiters()[0]+st.sval+stmt.getDelimiters()[1] );
                int j = st.nextToken();
                if( j != ']') st.pushBack();
            }
            else if( i == ')') {
                if( ctx instanceof FunctionContext ) {
                    stack.pop();
                    stack.peek().append(ctx.toString());
                }
                else {
                    ctx.append(")");
                }
            }
            else {
                ctx.append( (char)i+"" );
            }
        }
        return stack.pop().toString();
    }
    
    public static interface ParseContext {
        void append( String s );
        String toString();
    }
    
    public static class StringBufferParseContext implements ParseContext {
        StringBuilder sb = new StringBuilder();
        public void append(String s) {
            sb.append( s ); 
        }
        public String toString() {
            return sb.toString();
        }
    }
    
    public static class FunctionContext implements ParseContext {
        SqlDialectFunction function;
        private StringBuilder buff = new StringBuilder();
        public FunctionContext(SqlDialectFunction st) {
            this.function = st;
        }
        public void append(String s) {
            if( s.equals(",")) {
                function.addParam(buff.toString());
                buff.setLength(0);
            }
            else {
                buff.append(s);
            }
        }
        public String toString() {
            if( buff.length() > 0 ) {
                this.function .addParam(buff.toString());
            }
            return this.function.toString();       
        }
    }
    
    
    public static StreamTokenizer createStreamTokenizer( InputStream is ) {
        StreamTokenizer st = new StreamTokenizer(is);
        st.wordChars('_', '_');
        st.ordinaryChar(' ');
        st.ordinaryChars('0', '9');
        st.wordChars('0', '9');
        return st;
    }
    
}
