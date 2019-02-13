/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.TestCase;

/**
 *
 * @author rameses
 */
public class SQLTestUnit extends TestCase {
    
    public SQLTestUnit(String testName) {
        super(testName);
    }

    public void test1() throws Exception { 
        String sql = getContent( new File("test/test/sample.sql")); 
        SQLObject so = new SQLObject(sql, 0, 10, null);  
        so.parse(); 
        System.out.println( getPagingStatementDefault(so)); 
    }

    private String getContent( File file ) throws Exception {
        StringBuilder sb = new StringBuilder(); 
        FileInputStream inp = null; 
        try {
            inp = new FileInputStream( file);
            byte[] bytes = new byte[1024 * 64]; 
            int read = -1; 
            while ( (read=inp.read(bytes)) != -1 ) {
                sb.append(new String(bytes, 0, read)); 
            }
            return sb.toString(); 
        } finally {
            try { inp.close(); }catch(Throwable t){;} 
        }
    }

    private String getPagingStatementDefault( SQLObject so ) { 
        StringBuilder buff = new StringBuilder();
        buff.append( so.selectBuilder ).append(" ");
        
        if ( so.hasSelectTop ) { 
            buff.append( so.sqlSelectTop ); 
        } else if ( so.limit > 0 ) { 
            buff.append(" TOP "+ (so.limit + so.start)); 
        } else { 
            buff.append(" TOP 1000 "); 
        } 
        
        buff.append(" ROW_NUMBER() OVER (ORDER BY (SELECT 1)) AS _rownum_, ");
        
        if ( so.hasSelectTop ) {
            buff.append( so.sqlSelectCols ); 
        } else {
            buff.append( so.columnBuilder ); 
        }

        buff.append(" ");
        buff.append( so.fromBuilder ); 
        buff.append( so.whereBuilder ); 
        buff.append( so.groupBuilder ); 
        buff.append( so.havingBuilder ); 
        buff.append( so.orderBuilder ); 

        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT ");
        if ( so.limit > 0 ) {
            sb.append(" TOP " + so.limit); 
        } 
        sb.append(" * FROM ( ").append( buff ).append(" )xxx "); 
        if ( so.start >= 0 ) { 
            sb.append(" WHERE _rownum_ > "+ so.start ); 
            //sb.append(" ORDER BY _rownum_ "); 
        } 
        return sb.toString();           
    }     
    
    
    private static final Pattern FN_PATTERN = Pattern.compile("[a-zA-Z]\\w+\\(.*?\\)");

    private class SQLObject {
        
        private String sql; 
        private int start;
        private int limit;
        private String[] pagingKeys;
        
        StringBuilder selectBuilder = new StringBuilder();
        StringBuilder columnBuilder = new StringBuilder();
        StringBuilder fromBuilder = new StringBuilder();
        StringBuilder whereBuilder = new StringBuilder();
        StringBuilder groupBuilder = new StringBuilder();
        StringBuilder havingBuilder = new StringBuilder();
        StringBuilder orderBuilder = new StringBuilder();
        
        boolean hasSelectTop;
        String sqlSelectTop = null; 
        String sqlSelectCols = null; 
        
        SQLObject( String sql, int start, int limit, String[] pagingKeys ) {
            this.sql = sql; 
            this.start = start;
            this.limit = limit;
            this.pagingKeys = pagingKeys; 
        }
        
        void parse() {
            String ids = "objid";
            if( pagingKeys !=null && pagingKeys.length>0) {
                boolean firstTime = true;
                StringBuilder keys = new StringBuilder();
                for( String s: pagingKeys) {
                    if (!firstTime) keys.append("+");
                    else firstTime = false;
                    
                    keys.append( s );
                } 
                ids = keys.toString();
            }

            int STATE_SELECT = 0;
            int STATE_COLUMNS = 1;
            int STATE_FROM = 2;
            int STATE_WHERE = 3;
            int STATE_GROUP = 4;
            int STATE_HAVING = 5;
            int STATE_ORDER = 6;

            selectBuilder = new StringBuilder();
            columnBuilder = new StringBuilder();
            fromBuilder = new StringBuilder();
            whereBuilder = new StringBuilder();
            groupBuilder = new StringBuilder();
            havingBuilder = new StringBuilder();
            orderBuilder = new StringBuilder();

            StringBuilder currentBuilder = null;
            Stack stack = new Stack();
            int currentState = STATE_SELECT;
            boolean hasDistinct = false;

            StringTokenizer st = new StringTokenizer(sql.trim());
            while(st.hasMoreElements()) {
                String s = (String)st.nextElement(); 
                if( s.equalsIgnoreCase("select") && currentState <= STATE_SELECT  ) {
                    selectBuilder.append( s  );
                    currentBuilder = columnBuilder;
                    currentState = STATE_COLUMNS;
                }
                else if( s.equalsIgnoreCase("distinct")) {
                   selectBuilder.append( " DISTINCT " );
                }
                else if( s.equalsIgnoreCase("from") && currentState == STATE_COLUMNS && stack.empty()  ) {
                    currentBuilder = fromBuilder;
                    currentBuilder.append( " " + s );
                    currentState = STATE_FROM;
                } 
                else if( s.equalsIgnoreCase("where") && currentState == STATE_FROM && stack.empty()) {
                    currentBuilder = whereBuilder;
                    currentBuilder.append( " " + s );
                    currentState = STATE_WHERE;
                }
                else if( s.equalsIgnoreCase("group") && currentState <= STATE_WHERE && currentState != STATE_COLUMNS && stack.empty() ) {
                    currentBuilder = groupBuilder;
                    currentBuilder.append( " " + s );
                    currentState = STATE_GROUP;
                }
                else if( s.equalsIgnoreCase("having") && currentState <= STATE_GROUP && currentState != STATE_COLUMNS && stack.empty() ) {
                    currentBuilder = havingBuilder;
                    currentBuilder.append( " " + s );
                    currentState = STATE_HAVING;
                }
                //else if( s.equalsIgnoreCase("order") && currentState <= STATE_HAVING && currentState != STATE_COLUMNS && stack.empty() ) 
                else if( s.equalsIgnoreCase("order") ) {
                    currentBuilder = orderBuilder;
                    currentBuilder.append( " " + s );
                    currentState = STATE_ORDER;
                }
                else if(s.equals("(") || s.trim().startsWith("(") || s.trim().endsWith("(")) {
                    if( currentState != STATE_WHERE ) {
                        stack.push(true);
                    }
                    currentBuilder.append( " " + s );
                }
                else if(s.equals(")") || s.trim().startsWith(")") || s.trim().endsWith(")")) {
                    if( currentState != STATE_WHERE && !FN_PATTERN.matcher(s).matches() && !stack.isEmpty() ) {
                        stack.pop();
                    }
                    currentBuilder.append( " " + s );
                }
                else {
                    currentBuilder.append( " " + s );
                }
            } 

            Pattern p = Pattern.compile("(TOP[\\s]{1,}[0-9]{1,}[\\s]{1,}PERCENT).*?", Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE); 
            Matcher m = p.matcher( columnBuilder );
            if ( m.find() ) {
                sqlSelectTop = m.group();
                sqlSelectCols = columnBuilder.substring( m.end() ); 
                hasSelectTop = true; 

            } else {
                p = Pattern.compile("(TOP[\\s]{1,}[0-9]{1,}[\\s]{1,}).*?", Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE); 
                m = p.matcher( columnBuilder );
                if ( m.find() ) {
                    sqlSelectTop = m.group();
                    sqlSelectCols = columnBuilder.substring( m.end() ); 
                    hasSelectTop = true; 
                } 
            } 
        }    
    }     
}
