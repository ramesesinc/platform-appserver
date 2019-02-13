/*
 * EntityManager.java
 *
 * Created on August 15, 2010, 1:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.JoinLink;
import com.rameses.common.UpdateChangeHandler;
import com.rameses.osiris3.schema.RelationKey;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.schema.SchemaSerializer;
import com.rameses.osiris3.sql.FieldToMap;
import com.rameses.osiris3.sql.MapToField;
import com.rameses.osiris3.sql.SqlContext;
import com.rameses.osiris3.sql.SqlExecutor;
import com.rameses.osiris3.sql.SqlQuery;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author elmo
 */
public class EntityManager {

    private SqlContext sqlContext;
    private SchemaManager schemaManager;
    private boolean debug = true;
    private boolean transactionOpen = false;
    //newly added. This is for the name of the schema
    private String schemaName;
    //if true, complex fields are separated by underscores.
    //example : person.firstname will be person_firstname in db
    private boolean resolveNested = true;
    private EntityManagerModel model;
    private EntityManagerInvoker invoker;
    
    private EntityManagerProcessor processor;
    
    public EntityManager(SchemaManager scm, SqlContext sqlContext, String schemaName) {
        this.sqlContext = sqlContext;
        this.schemaManager = scm;
        this.processor = new EntityManagerProcessor(sqlContext, sqlContext.getDialect() );
        if(schemaName!=null) setName(schemaName);
    }

    public EntityManager(SchemaManager scm, SqlContext sqlContext) {
        this(scm, sqlContext, null);
    }

    public void setSqlContext(SqlContext ctx) {
        if (transactionOpen) {
            throw new RuntimeException("SqlContext cannot be set at this time because transaction is currently open");
        }
        this.sqlContext = ctx;
    }

    public SqlContext getSqlContext() {
        return this.sqlContext;
    }

    public EntityManager shift(String name) {
        return setName(name);
    }
    
    public EntityManager setName(String name) {
        if(this.schemaName!=null && schemaName.equals(name)) return this;
        this.schemaName = name;
        SchemaElement elem = schemaManager.getElement(name);
        model = new EntityManagerModel(elem);
        return this;
    }

    public EntityManagerModel getModel() {
        if( model == null ) throw new RuntimeException("Please specify an element name");
        return model;
    }
    
    //this should be called after every call.
    private void clearModel() {
        SchemaElement elem = schemaManager.getElement(this.schemaName);
        model = new EntityManagerModel(elem);
    }

    
    /**************************************************************************
     *  CREATE
     **************************************************************************/ 
    public Object create(Object data) {
        try {
            if (!(data instanceof Map)) {
                throw new Exception("EntityManager.create error. Data passed must be a map");
            }
            processor.create(getModel(), (Map)data);
            clearModel();
            return data;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Object create(String schemaName, Object data) {
        return create(schemaName, data, true);
    }

     //we will have a separate implementation of the following because 
    //many applications are already using this. we will deprecate later
    public Object create(String schemaName, Object data, boolean validate) {
        try {
            if (!(data instanceof Map)) {
                throw new Exception("EntityManager.create error. Data passed must be a map");
            }
            if( !schemaName.equals(this.schemaName) ) {
                 setName( schemaName );
            }
            Map mdata = (Map)data;
            
                //EntityValidator.validate(mdata, getModel().getElement());
            //}
            processor.create(getModel(), mdata);
            clearModel();
            return mdata;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public List list(int start, int size)  {
        try {
            getModel().setStart(start);
            getModel().setLimit(size);
            List list = processor.fetchList(getModel());
            clearModel();
            return list;
        } catch (RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    //default list size is 50
    public List list() throws Exception {
        try {
            List list = processor.fetchList(getModel());
            clearModel();
            return list;
        } catch (RuntimeException re) {
            throw re;
        } catch(Exception e) { 
            throw new RuntimeException(e.getMessage(), e); 
        } 
    }
    
    public EntityManager vars( String name, String expr ) {
        getModel().getVars().put(name, expr);
        return this;
    }
    
    //get first record
    public Map first() {
        try {
            getModel().setLimit(1);
            getModel().setStart(0);
            if( !getModel().hasCriteria() ) {
                throw new RuntimeException("Please specify a criteria, finder or where element for first method");
            }
            Map m = processor.fetchFirst(getModel(), 0);
            clearModel();
            return m;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public Map first(int level) {
        try {
            getModel().setLimit(1);
            getModel().setStart(0);
            if( !getModel().hasCriteria() ) {
                throw new RuntimeException("Please specify a criteria, finder or where element for first method");
            }
            Map m = processor.fetchFirst(getModel(), level);
            clearModel();
            return m;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public Object merge(Map data) {
        try {
            Map m =  processor.merge(getModel(), data);
            clearModel();
            return m;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * if there are no records found, this function returns null
     */
    public Object read(Object data) {
        return read(this.schemaName, data, null);
    }
    
    public Object read(String schemaName, Object data) {
        return read(schemaName, data, null);
    }

    //read will fetch each data everytime, there are no joins, each 
    //table will be fetched individually and merged. Read also is dependent
    //on the primary keys
    public Object read(String schemaName, Object data, Map options) {
        try {
            if (!(data instanceof Map)) {
                throw new Exception("EntityManager.create error. Data passed must be a map");
            }
            if( !schemaName.equals(this.schemaName) ) {
                 setName( schemaName );
            }
            processor.buildFindersFromPrimaryKeys(getModel(), (Map)data);
            Object d = processor.fetchFirst(getModel(), 1);
            clearModel();
            return d;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            System.out.println("error in read ->" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    /**
     * applicable for updates with mapped parameters for example
     * SET amount = amount + :amount. 
     * @param data - this is a map bec. if we make this as Object it will conflict with
     *     other update methods
     * @param params in case there are parameters in the expression, this will be the 
     *     values mapped 
     * @return 
     */
    public Object update(Map data, Map params) {
        try {
            //processor.buildFindersFromPrimaryKeys(getModel(), (Map)data);
            DataFillUtil.fillInitialData(getModel().getElement(), (Map)data);
            Object p = processor.update(getModel(), (Map)data, params);
            clearModel();
            return p;
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
    * This is applicable for updates passed as setExpr.
    */
    public Object update(Object data) {
        try {
            if( !(data instanceof Map )) {
                throw new Exception("EntityManager.update Data must be an instanceof Map ");
            }
            if( !getModel().hasCriteria() ) {
                processor.buildFindersFromPrimaryKeys(getModel(), (Map)data);
            }
            Object p = processor.update(getModel(), (Map)data);
            clearModel();
            return p;
        } catch (RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }
    
    public Object update(String schemaName, Object data) {
        return update(schemaName, data, null, null, true, true);
    }

    public Object update(String schemaName, Object data, boolean validate) {
        return update(schemaName, data, null, null, validate, true);
    }

    public Object update(String schemaName, Object data, Map vars) {
        return update(schemaName, data, vars, null, true, true);
    }

    public Object update(String schemaName, Object data, UpdateChangeHandler h) {
        return update(schemaName, data, null, h, true, true);
    }

    //added version handling of changes during updates
    public Object update(String schemaName, Object data, Map vars, UpdateChangeHandler vhandler, boolean validate, boolean read) {
        try {
            if (!(data instanceof Map)) {
                throw new Exception("EntityManager.create error. Data passed must be a map");
            }
            if( !schemaName.equals(this.schemaName) ) {
                 setName( schemaName );
            }
            Map mdata = (Map)data;   
            /*
            if (validate) {
                validate(elem, data, model.getIncludeFields(), null);
            } 
            */ 
            if( !getModel().hasCriteria() ) {
                processor.buildFindersFromPrimaryKeys(getModel(), (Map)data);
            }
            Object p = processor.update(getModel(), mdata);
            clearModel();
            return p;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    //special function to avoid reading the data before updating
    public Object updateImmediate(String schemaName, Object data) {
        return update(schemaName, data, null, null, true, false);
    }

    public void delete() {
        try {
            if( !getModel().hasCriteria() ) {
                throw new RuntimeException("Please specify a criteria, finder or where element for delete");
            }
            processor.delete(getModel());
            clearModel();
        } catch (RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void delete(String schemaName) {
        delete(schemaName, null);
    }
    public void delete( Map data ) {
        delete(schemaName, data);
    }
    public void delete(String schemaName, Object data) {
        try {
            if( !schemaName.equals(this.schemaName) ) {
                 setName( schemaName );
            }
            if( !getModel().hasCriteria() ) {
                processor.buildFindersFromPrimaryKeys(getModel(), (Map)data);
            }
            processor.delete(getModel());
            clearModel();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * *
     * extended methods in the DefaultEntityManager
     */
    public boolean isDebug() {
        return processor.isDebug();
    }

    public void setDebug(boolean debug) {
        this.processor.setDebug(debug);
    }

    public Map getSchema() {
        return getModel().getElement().createView().getSchema();
    }
    
    public Map getSchema(String colNames) {
        return getModel().getElement().createView().getSchema( colNames );
    }
    
    //this checks if a record exists. This just returns true or false
    public boolean exists() {
       return processor.checkExists(getModel());
    }
    
    /*
    public Object createModel(String schemaName) {
        return getModel().getElement().toMap();
    }
    */ 
    
    public void validate(String schemaName, Object data) {
        SchemaElement elem = schemaManager.getElement(schemaName);
        validate( elem, data, null, null );
    }

    public void validate(SchemaElement elem, Object data, String includeFields, String excludeFields) {
        try {
            ValidationResult vr = ValidationUtil.validate(data, elem, includeFields, excludeFields);
            if (vr.hasErrors()) {
                throw new Exception(vr.toString());
            }
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * add a map serializer also later.
     */
    public SchemaSerializer getSerializer() {
        return schemaManager.getSerializer();
    }

    public SchemaManager getSchemaManager() {
        return schemaManager;
    }

    /**
     * This is a generic save routine merged as one call. create = if true will
     * insert the record. update = if true will update the record. if
     * create=true and update=false, this is insert only. you get the picture.
     */
    public Object save(Object data) {
        return save(this.schemaName, data);
    }    
    public Object save(String schemaName, Object data) {
        return save(schemaName, data, null);
    }

    public Object save(String schemaName, Object data, Map vars) {
        return save(schemaName, data, true, true, vars);
    }

    public Object save(String schemaName, Object data, boolean create, boolean update) {
        return save(schemaName, data, create, update, null, null, true);
    }

    public Object save(String schemaName, Object data, boolean create, boolean update, Map vars) {
        return save(schemaName, data, create, update, vars, null, true);
    }

    public Object save(String schemaName, Object data, boolean create, boolean update, boolean validate) {
        return save(schemaName, data, create, update, null, null, validate);
    }

    public Object save(String schemaName, Object data, boolean create, boolean update, Map vars, UpdateChangeHandler vhandler, boolean validate) {
        if( !schemaName.equals(this.schemaName) ) {
            setName( schemaName );
        }
        boolean exists = false;
        try {
            if( !getModel().hasCriteria() ) {
                processor.buildFindersFromPrimaryKeys(getModel(), (Map)data);
            }
            exists = exists();
        }
        catch(Throwable ign) {;}
        
        if (create == true && update == true) {
            if (!exists) {
                return create(schemaName, data, validate);
            } else {
                return update(schemaName, data, vars, vhandler, validate, true);
            }
        } else if (create == true && update == false) {
            return create(schemaName, data, validate);
        } else if (create == false && update == true) {
            if (!exists) {
                throw new RuntimeException("Record for update does not exist");
            }
            return update(schemaName, data, vars, vhandler, validate, true);
        } else {
            return data;
        }
    }

    public boolean isTransactionOpen() {
        return transactionOpen;
    }

    public Map mapToField(Map data) {
        return MapToField.convert(data, null);
    }

    public Map mapToField(Map data, String excludeFields) {
        return MapToField.convert(data, excludeFields);
    }

    public Map fieldToMap(Map data) {
        return fieldToMap(data, null);
    }

    public Map fieldToMap(Map data, String excludeFields) {
        return FieldToMap.convert(data, excludeFields);
    }

    public boolean isResolveNested() {
        return resolveNested;
    }

    public void setResolveNested(boolean convertComplex) {
        this.resolveNested = resolveNested;
    }

    public EntityManager find(Map params) {
        //replace all field names with underscores in case there are periods
        for(  Object k: params.entrySet() ) {
            Map.Entry me = (Map.Entry)k;
            String s = me.getKey().toString().trim().replace(".", "_");
            getModel().getFinders().put(s, me.getValue());    
        }
        return this;
    }
    
    public EntityManager select(String fldExpr) {
        getModel().setSelectFields(fldExpr);
        return this;
    }
    
    public EntityManager where(String expr) {
       getModel().setWhereElement(expr, null);
        return this;
    }
    
    public EntityManager where(String expr,  Map params) {
        getModel().setWhereElement(expr, params);
        return this;
    }
    
    public EntityManager orderBy(String fieldname) {
        getModel().setOrderExpr(fieldname);
        return this;
    }
    
    public EntityManager limit(int start, int limit) {
        getModel().setStart(start);
        getModel().setLimit(limit);
        return this;
    }
    
    public EntityManager limit(int limit) {
        getModel().setStart(0);
        getModel().setLimit(limit);
        return this;
    }
    
    public EntityManager setStart(int start) {
        getModel().setStart(start);
        return this;
    }
    
    public EntityManager setLimit(int limit) {
        getModel().setLimit(limit);
        return this;
    }
    
    public EntityManager groupBy( String expr ) {
        getModel().setGroupByExpr(expr);
        return this;
    }

    public EntityManager orWhere( String expr ) {
        getModel().addOrWhereElement(expr, null);
        return this;
    }
    
    public EntityManager orWhere( String expr, Map params ) {
        getModel().addOrWhereElement(expr, params);
        return this;
    }

    public void setInvoker(EntityManagerInvoker inv) {
        this.invoker = inv;
    }

    //used by the data context. if starts with find, it returns single record. 
    //If it starts with get, it returns list
    public Object invokeSqlMethod( String methodName, Object args ) throws Exception {
        String finalMethodName = this.schemaName+":"+methodName;
        Map m = null;
        if( args == null ) {
            m = null;
        }
        else if(args instanceof Object[]) {
            Object[] ao = (Object[])args; 
            if( ao.length > 0 ) {
                if(! (ao[0] instanceof Map) ) 
                    throw new Exception("Unrecognized parameter for invokeSqlMethod. Must be map or Object[]");
                m = (Map) ao[0] ;
            }
        }
        else if (args instanceof Map)  {
             m = (Map)args;
        }
        else {
            throw new Exception("Unrecognized parameter for invokeSqlMethod. Must be map or Object[]");
        }
        
        if( methodName.startsWith("find") || methodName.startsWith("get") ) {
            SqlQuery sq = sqlContext.createNamedQuery( finalMethodName );   
            sq.setFetchHandler(new DataMapFetchHandler(getModel().getSchemaView())); 
            if(m!=null) {
                sq.setVars(m).setParameters(m);
                if(m.containsKey("_start")) {
                    int s = Integer.parseInt(m.get("_start")+"");
                    sq.setFirstResult( s );
                }
                if(m.containsKey("_limit")) {
                    int l = Integer.parseInt(m.get("_limit")+"");
                    sq.setMaxResults( l );
                }
            }
            if(methodName.startsWith("find"))
                return sq.getSingleResult();
            else 
                return sq.getResultList();
        }
        else {
            SqlExecutor sqe = sqlContext.createNamedExecutor( finalMethodName );    
            if(m!=null) {
                sqe.setVars(m).setParameters(m);
            }
            return sqe.execute();
        }
    }
    
    public SqlQuery createQuery(String sql) {
        return this.getSqlContext().createQuery(sql);
    }
    
    public SqlExecutor createExecutor(String sql) {
        return this.getSqlContext().createExecutor(sql);
    }

    public SqlExpression createExpr( String statement ) {
        return new SqlExpression(statement);
    }
    
    public SubQueryModel subquery() {
        return new SubQueryModel(getModel()); 
    }
    
    public EntityManager addSubquery(String name, SubQueryModel model) {
        getModel().addSubquery(name, model);
        return this;
    }
    
    public EntityManager join(String elemName, String alias, Map keys ) {
        SchemaElement elem = schemaManager.getElement(elemName);
        JoinLink jl = new JoinLink(elem, alias);
        jl.setRequired(true);
        for( Object m: keys.entrySet() ) {
            Map.Entry<String, String> me = (Map.Entry)m;
            RelationKey rk = new RelationKey();
            rk.setField(me.getKey());
            rk.setTarget(me.getValue());
            jl.getRelationKeys().add(rk);
        }
        jl.setJoinType("INNER");
        getModel().addJoinLink(jl);
        return this;
    }
    
     public EntityManager leftJoin(String elemName, String alias, Map keys ) {
        SchemaElement elem = schemaManager.getElement(elemName);
        JoinLink jl = new JoinLink(elem, alias);
        jl.setRequired(false);
        for( Object m: keys.entrySet() ) {
            Map.Entry<String, String> me = (Map.Entry)m;
            RelationKey rk = new RelationKey();
            rk.setField(me.getKey());
            rk.setTarget(me.getValue());
            jl.getRelationKeys().add(rk);
        }
        jl.setJoinType("LEFT");
        getModel().addJoinLink(jl);
        return this;
    }
    
    //returns a single value. The first element it finds
    public Object val() {
        try {
            Map m = first();
            if( m == null || m.size() == 0 ) return null;
            
            Iterator keys = m.keySet().iterator(); 
            while (keys.hasNext()) {
                Object o = keys.next();
                if ( o == null || o.toString().matches("_.*?_")) continue; 

                return m.get( o ); 
            } 
            return null; 
        } catch(Exception e) {
            throw new RuntimeException("Error in val. "+e.getMessage());
        }
    } 

    public EntityManager pagingKeys( String pagingKeys ) {
        getModel().setPagingKeys( pagingKeys ); 
        return this; 
    }
    
    public String getStatement() throws Exception {
        return processor.buildStatement(getModel());
    }
}
