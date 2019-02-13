package com.rameses.sql.dialect.mssql;


import com.rameses.osiris3.sql.SqlUnit;
import java.util.ArrayList;
import java.util.List;




public class MsSqlCrudSqlBuilder  {
    /*
    public SqlUnit getCreateSqlUnit(CrudModel cp) {
        List paramNames = new ArrayList();
        StringBuffer sb = new StringBuffer();
        StringBuffer tail = new StringBuffer();
        sb.append( "INSERT INTO " + escapeField(cp.getTableName()) + " (");
        tail.append( "(" );
        boolean firstPass = true;
        
        //loop the fields.
        for(CrudModel.CrudField cf : cp.getFields()) {
            if(cf.isLinked()) continue;
            if(firstPass)
                firstPass = false;
            else {
                sb.append(",");
                tail.append(",");
            }
            sb.append( escapeField(cf.getFieldName()) );
            tail.append( "?");
            paramNames.add( cf.getName() );
        }
        sb.append( ")");
        tail.append( ")");
        String stmt = sb.append( " VALUES ").append(tail).toString();
        return new SqlUnit(stmt, paramNames);
    }
    
    
    public SqlUnit getReadSqlUnit(CrudModel cp) {
        List paramNames = new ArrayList();
        List<CrudField> primKeys = new ArrayList();
        
        StringBuffer sb = new StringBuffer();
        sb.append( "SELECT ");
        boolean firstPass = true;
        for(CrudModel.CrudField cf : cp.getFields()) {
            if(cf.isPrimary()) {
                primKeys.add(cf);
                paramNames.add( cf.getName() );
            }
            if(firstPass)
                firstPass = false;
            else {
                sb.append(",");
            }
            if(!cf.isLinked()) sb.append( escapeField(cp.getTableName()) + ".");
            sb.append( escapeField(cf.getFieldName()) + " AS " + cf.getName());
        }
        sb.append( " FROM " + escapeField(cp.getTableName()));
        
        if(cp.getLinkTable()!=null) sb.append( escapeField(cp.getLinkTable()));
        
        if (primKeys.size()== 0)
            throw new RuntimeException("There must be at least one primary key for CRUD getReadSqlCache");
        
        sb.append( " WHERE ");
        int i = 0;
        for(CrudModel.CrudField p : primKeys) {
            if( i>0) sb.append( " AND " );
            sb.append( escapeField(cp.getTableName()) +"."+ escapeField(p.getFieldName()) + "=?" );
            i++;
        }
        String stmt = sb.toString();
        return new SqlUnit(stmt, paramNames);
    }
    
    
    public SqlUnit getUpdateSqlUnit(CrudModel cp) {
        List paramNames = new ArrayList();
        List<CrudField> primKeys = new ArrayList();
        StringBuffer sb = new StringBuffer();
        sb.append( "UPDATE " + escapeField(cp.getTableName()) + " SET ");
        boolean firstPass = true;
        for(CrudModel.CrudField cf : cp.getFields()) {
            if(cf.isLinked()) continue;
            if(cf.isPrimary()) {
                primKeys.add(cf);
                continue;
            }
            if(firstPass)
                firstPass = false;
            else {
                sb.append(",");
            }
            sb.append( escapeField(cf.getFieldName()) + "=?" );
            paramNames.add( cf.getName() );
        }
        if (primKeys.size()== 0)
            throw new RuntimeException("There must be at least one primary key for CRUD getReadSqlCache");
        
        sb.append( " WHERE ");
        int i = 0;
        for(CrudModel.CrudField p : primKeys) {
            if( i>0) sb.append( " AND " );
            sb.append( escapeField(p.getFieldName()) + "=?" );
            paramNames.add( p.getName() );
            i++;
        }
        String stmt = sb.toString();
        return new SqlUnit(stmt, paramNames);
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="SQL DELETE">
    public SqlUnit getDeleteSqlUnit(CrudModel cp) {
        List paramNames = new ArrayList();
        List<CrudField> primKeys = new ArrayList();
        StringBuffer sb = new StringBuffer();
        sb.append( "DELETE FROM " + escapeField(cp.getTableName()));
        boolean firstPass = true;
        for(CrudModel.CrudField cf : cp.getFields()) {
            if(cf.isPrimary()) {
                primKeys.add(cf);
                paramNames.add( cf.getName() );
            }
        }
        if (primKeys.size()== 0)
            throw new RuntimeException("There must be at least one primary key for CRUD getReadSqlCache");
        
        sb.append( " WHERE ");
        int i = 0;
        for(CrudModel.CrudField p : primKeys) {
            if( i>0) sb.append( " AND " );
            sb.append( escapeField(p.getFieldName()) + "=?" );
            i++;
        }
        String stmt = sb.toString();
        return new SqlUnit(stmt, paramNames);
    }
    // <editor-fold defaultstate="collapsed" desc="SQL LIST">
    public SqlUnit getListSqlUnit(CrudModel cp, String xalias) {
        String alias = cp.getAlias();
        if(alias==null || alias.trim().length()==0) alias = xalias;
        alias = alias.replaceAll("/", "_");
        
        List<CrudField> primKeys = new ArrayList();
        List paramNames = new ArrayList();
        StringBuffer sb = new StringBuffer();
        sb.append( "SELECT ${columns} FROM (");
        sb.append( "SELECT ");
        boolean firstPass = true;
        for(CrudModel.CrudField cf : cp.getFields()) {
            if(firstPass)
                firstPass = false;
            else {
                sb.append(",");
            }
            if(!cf.isLinked()) sb.append(escapeField(cp.getTableName())+".");
            sb.append( escapeField(cf.getFieldName()) + " AS " + cf.getName());
        }
        sb.append( " FROM " + escapeField(cp.getTableName()));
        if(cp.getLinkTable()!=null) sb.append(escapeField(cp.getLinkTable()));
        sb.append( " ) " + alias );
        sb.append( " ${condition}");
        
        String stmt = sb.toString();
        return new SqlUnit(stmt, paramNames);
    }
    
    //helper method
    private String escapeField( String name ) {
        return "[" + name + "]";
    }
    */
}