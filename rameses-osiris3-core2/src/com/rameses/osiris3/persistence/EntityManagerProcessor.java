package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.AbstractSchemaView;
import com.rameses.osiris3.schema.ComplexField;
import com.rameses.osiris3.schema.OneToManyLink;
import com.rameses.osiris3.schema.RelationKey;
import com.rameses.osiris3.schema.SchemaElement;
import com.rameses.osiris3.schema.SchemaField;
import com.rameses.osiris3.schema.SchemaRelation;
import com.rameses.osiris3.schema.SchemaView;
import com.rameses.osiris3.schema.SchemaViewField;
import com.rameses.osiris3.schema.SchemaViewRelationField;
import com.rameses.osiris3.schema.SimpleField;
import com.rameses.osiris3.sql.SqlContext;
import com.rameses.osiris3.sql.SqlDialect;
import com.rameses.osiris3.sql.SqlDialectModel;
import com.rameses.osiris3.sql.SqlExecutor;
import com.rameses.osiris3.sql.SqlQuery;
import com.rameses.osiris3.sql.SqlUnit;
import com.rameses.osiris3.sql.SqlUnitCache;
import com.rameses.util.EntityUtil;
import com.rameses.util.ValueUtil;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class EntityManagerProcessor {

    private boolean debug = false;
    private SqlDialect sqlDialect;
    private SqlContext sqlContext;

    public EntityManagerProcessor(SqlContext sqc, SqlDialect stmt) {
        this.sqlContext = sqc;
        this.sqlDialect = stmt;
    }

    public void executeUpdate(SqlDialectModel sqm, Map baseData, Map vars) throws Exception {
        SqlUnit squ = SqlUnitCache.getSqlUnit(sqm, this.sqlDialect);
        SqlExecutor exec = this.sqlContext.createExecutor(squ);
        if (vars != null) {
            exec.setVars(vars);
        }
        for (Object o : squ.getParamNames()) {
            String s = o.toString();
            exec.setParameter(s, baseData.get(s));
        }

        if (this.debug) {
            System.out.println("-> " + squ.getStatement());
            for (Object o : squ.getParamNames()) {
                String s = o.toString();
                System.out.println("param->"+s+"="+baseData.get(s));
            }
            if ((vars != null) && (vars.size() > 0)) {
                System.out.println("vars");
                for (Object o: vars.entrySet()) {
                    Map.Entry me = (Map.Entry) o;
                    System.out.println(me.getKey()+"="+me.getValue());
                }
            }
        }
        exec.execute();
    }

    public SqlQuery createQuery(SqlDialectModel sqm, Map params, Map vars)
            throws Exception {
        SqlUnit squ = SqlUnitCache.getSqlUnit(sqm, this.sqlDialect);
        SqlQuery qry = this.sqlContext.createQuery(squ);
        if ((sqm.getStart() > 0) || (sqm.getLimit() > 0)) {
            params.put("_start", Integer.valueOf(sqm.getStart()));
            params.put("_limit", Integer.valueOf(sqm.getLimit()));
        }
        if (vars != null) {
            qry.setVars(vars);
        }
        for (Object o: squ.getParamNames()) {
            String s = o.toString();
            qry.setParameter(s, params.get(s));
        }

        if (this.debug) {
            System.out.println("-> " + squ.getStatement());
            for (Object o : squ.getParamNames()) {
                String s = o.toString();
                System.out.println("param->"+s+"="+params.get(s));
            }
            if ((vars != null) && (vars.size() > 0)) {
                System.out.println("vars");
                for (Object o: vars.entrySet()) {
                    Map.Entry me = (Map.Entry) o;
                    System.out.println(me.getKey()+"="+me.getValue());
                }
            }
        }
        return qry;
    }

    public SqlUnit getSqlUnit(SqlDialectModel sqm) throws Exception {
        return SqlUnitCache.getSqlUnit(sqm, this.sqlDialect);
    }

    public String buildStatement(SqlDialectModel sqm) throws Exception {
        SqlUnit squ = SqlUnitCache.getSqlUnit(sqm, this.sqlDialect);
        return squ.getStatement();
    }

    public Map create(EntityManagerModel model, Map data) throws Exception {
        DataFillUtil.fillInitialData(model.getElement(), data);
        ValidationResult vr = ValidationUtil.validate(data, model.getElement());
        if(vr.hasErrors()) throw new Exception(vr.toString());
        SchemaView svw = model.getSchemaView();
        Map sqlModelMap = new LinkedHashMap();
        SqlDialectModelBuilder.buildCreateSqlModels(svw, sqlModelMap);

        resolveUpdateForMerge( model, data );         
        
        create(svw, data, sqlModelMap, model.getVars());
        return data;
    }

    public void create(SchemaView svw, Map rawData, Map<String, SqlDialectModel> sqlModelMap, Map vars) throws Exception {
        Map baseData = DataTransposer.prepareDataForInsert(svw, rawData);
        createBase(svw, baseData, sqlModelMap, vars);
        createOneToOneLinks(svw, baseData, sqlModelMap, vars);
        createOneToManyLinks(svw, rawData, sqlModelMap, vars);
    }

    public void createBase(AbstractSchemaView svw, Map data, Map<String, SqlDialectModel> sqlModelMap, Map vars) throws Exception {
        if (svw.getExtendsView() != null) {
            createBase(svw.getExtendsView(), data, sqlModelMap, vars);
        }

        SqlDialectModel sqlModel = (SqlDialectModel) sqlModelMap.get(svw.getName());
        executeUpdate(sqlModel, data, vars);
    }

    public void createOneToOneLinks(AbstractSchemaView vw, Map data, Map<String, SqlDialectModel> modelMap, Map vars) throws Exception {
        if (vw.getExtendsView() != null) {
            createOneToOneLinks(vw.getExtendsView(), data, modelMap, vars);
        }
        for (AbstractSchemaView lnkVw : vw.getOneToOneViews()) {
            createBase(lnkVw, data, modelMap, vars);
            String n = lnkVw.getName()+":one--to-one-update";
            SqlDialectModel updateModel = (SqlDialectModel) modelMap.get(n);
            if (updateModel == null) {
                updateModel = SqlDialectModelBuilder.buildOneToOneUpdateLinkedId(lnkVw);
                modelMap.put(n, updateModel);
            }
            if (updateModel == null) {
                throw new Exception("SqlProcessor.create error. sql update for one to one relationship not found");
            }
            executeUpdate(updateModel, data, vars);
        }
    }

    public void createOneToManyLinks(AbstractSchemaView vw, Map rawData, Map<String, SqlDialectModel> modelMap, Map vars) throws Exception {
        if (vw.getExtendsView() != null) {
            createOneToManyLinks(vw.getExtendsView(), rawData, modelMap, vars);
        }
        for (SchemaRelation sr : vw.getElement().getOneToManyRelationships()) {
            Object d = EntityUtil.getNestedValue(rawData, sr.getName());
            if (d != null) {
                SchemaView svw = sr.getLinkedElement().createView();
                if (!modelMap.containsKey(sr.getLinkedElement().getName())) {
                    SqlDialectModelBuilder.buildCreateSqlModels(svw, modelMap);
                }
                List list = (List) d;
                for (Object o: list) {
                    if (o instanceof Map) {
                        create(svw, (Map) o, modelMap, vars);
                    }
                }
            }
        }
    }

    public void buildFindersFromPrimaryKeys(EntityManagerModel entityModel, Map data)  {
        Map finders = DataUtil.buildFinderFromPrimaryKeys(entityModel.getElement(), data);
        if( finders == null ) throw new RuntimeException("Please specify the primary keys");
        entityModel.getFinders().putAll(finders);
    }
    
    public Map update(EntityManagerModel model, Map odata)  throws Exception {
        return update(model, odata, null);
    }

    public Map update(EntityManagerModel entityModel, Map odata, Map updateParams) throws Exception {
        
        if ((odata == null) || (odata.size() == 0)) {
            throw new Exception("update error. data must have at least one value");
        }
        if ((entityModel.getFinders().size() == 0) && (entityModel.getWhereElement() == null)) {
            throw new Exception("update error. finder or where must be specified");
        }

        resolveUpdateForMerge( entityModel, odata ); 
        
        SchemaView svw = entityModel.getSchemaView();
        Map data = DataTransposer.prepareDataForUpdate(svw, odata);
        Map<String, SqlDialectModel> modelMap = SqlDialectModelBuilder.buildUpdateSqlModels(entityModel, data);
        Map params = new HashMap(); 
        params.putAll(data);
        
        
        if( entityModel.getFinders()!=null) {
            params.putAll(entityModel.getFinders());
        }
        if( entityModel.getWhereParams()!=null) {
            params.putAll(entityModel.getWhereParams());
        }
        if (updateParams != null) {
            params.putAll(updateParams);
        }
        Map vars = entityModel.getVars();
        for (SqlDialectModel sqlModel : modelMap.values()) {
            executeUpdate(sqlModel, params, vars);
        }
        //the entity model and params are passed just in case this is an update.
        updateOneToMany(svw, odata, entityModel, params);
        return odata;
    }

    public void updateOneToMany(SchemaView svw, Map parent, EntityManagerModel entityModel, Map params ) throws Exception {
                //update one to many links. loop each 
        if( svw.getOneToManyLinks()==null  ) return;
        //check first if the parent has primary keys if not, we'll have to load it
        for(OneToManyLink oml: svw.getOneToManyLinks() ) {
            String sname = oml.getName();
            List items = null;
            try { 
                Object itm = EntityUtil.getNestedValue(parent, sname); 
                if(itm!=null && (itm instanceof List)) items = (List)itm;
            } catch(Exception ign){;}
            if( items !=null ) {
                //we try to retrieve the objid of the parent because this will be used in populating new items.
                //if parent's primary keys do not exist we need to retrieve it.
                for( SimpleField sf: svw.getElement().getPrimaryKeys()) {
                    Object kval = EntityUtil.getNestedValue(parent, sf.getName() );
                    if( kval == null ) {
                        SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSelectIndexedKeys( entityModel );
                        Map b = (Map)createQuery(sqlModel, params, null).getSingleResult();
                        if(b==null) throw new Exception("Update One to many error. Record not found for parent");
                        parent.putAll(b);
                        break;        
                    }
                }
                EntityManagerModel itemModel = new EntityManagerModel(oml.getRelation().getLinkedElement());
                for(Object m: items) {
                    if(! (m instanceof Map) ) continue;
                    saveItem( itemModel, (Map)m, oml.getRelation(), parent);
                }
            };
            //we'll also remove items that are markeed as deleted.
            List deletedItems = null;
            try { 
                Object itm = EntityUtil.getNestedValue(parent, sname+"::deleted"); 
                if(itm!=null && (itm instanceof List)) deletedItems = (List)itm;
            } catch(Exception ign){;}
            if(deletedItems !=null ) {
                EntityManagerModel itemModel = new EntityManagerModel(oml.getRelation().getLinkedElement());
                for(Object m: deletedItems) {
                    if(! (m instanceof Map) ) continue;
                    buildFindersFromPrimaryKeys(itemModel, (Map)m);
                    delete(itemModel);
                }
            };
        }
    }
    
    public Object saveItem(EntityManagerModel entityModel, Map data, SchemaRelation rel, Map parent) throws Exception {
        boolean exists = false;
        try {
            buildFindersFromPrimaryKeys(entityModel, (Map)data);
            exists = checkExists(entityModel);
        }
        catch(Exception ign) {;}
        Map odata = data;
        if(exists) {
            buildFindersFromPrimaryKeys(entityModel, (Map)data);
            odata = update( entityModel, data );
        }
        else {
            //fill in the relationships
            for(RelationKey rk: rel.getRelationKeys()) {
                Object kval = EntityUtil.getNestedValue(parent,rk.getField());
                //if rel key is null, we need to try to retreive the parent
                
                
                EntityUtil.putNestedValue(data, rk.getTarget(), kval);
            }
            odata = create( entityModel, data );
        }
        return odata;
    }    
    
    public Map merge(EntityManagerModel model, Map data) throws Exception {
        Map m = update(model, data);
        return m;
    }

    public List fetchList(EntityManagerModel model) throws Exception {
        SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSelectSqlModel(model);
        Map parms = new HashMap();
        parms.putAll(DataTransposer.flatten(model.getFinders(), "_"));
        parms.putAll(model.getWhereParams());
        //add the or where parameters
        if( model.getOrWhereList()!=null && model.getOrWhereList().size()>0 ) {
            for( EntityManagerModel.WhereElement we: model.getOrWhereList() ) {
                parms.putAll( we.getParams() );
            }
        }
        Map vars = model.getVars();
        SqlQuery sqlQry = createQuery(sqlModel, parms, vars);
        sqlQry.setFetchHandler(new DataMapFetchHandler(model.getSchemaView()));
        return sqlQry.getResultList();
    }

    public Map fetchFirst(EntityManagerModel model, int nestLevel) throws Exception {
        SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSelectSqlModel(model);
        Map parms = new HashMap();
        parms.putAll(DataTransposer.flatten(model.getFinders(), "_"));
        parms.putAll(model.getWhereParams());
        sqlModel.setStart(0);
        sqlModel.setLimit(1);
        Map vars = model.getVars();
        SqlQuery sqlQry = createQuery(sqlModel, parms, vars);
        sqlQry.setFetchHandler(new DataMapFetchHandler(model.getSchemaView()));
        Map result = (Map) sqlQry.getSingleResult();
        ComplexField cf = model.getElement().findMergeComplexField(); 
        resolveResultForMerge(cf, result); 
        if (nestLevel > 0) { 
            fetchSubItems(model, result, 1, nestLevel);
        }
        return result;
    }

    public boolean checkExists(EntityManagerModel entityModel) {
         try {
            //this will translate to select 1 from table
            entityModel.setSelectFields("count:{ 1 }");
            Map r = fetchFirst(entityModel, 0);
            if(r!=null && Integer.parseInt(r.get("count").toString()) > 0 ) {
                return true;
            }
            return false;
        }
        catch(Exception e) {
            return false;
        }
    }
    
    public void fetchSubItems(EntityManagerModel parentModel, Map parent, int level, int nestLevel) throws Exception {
        for (SchemaRelation sr : parentModel.getElement().getOneToManyRelationships()) {
            if( sr.isLazyLoad() ) continue;
            EntityManagerModel subModel = new EntityManagerModel(sr.getLinkedElement());
            for (RelationKey rk : sr.getRelationKeys()) {
                subModel.getFinders().put(rk.getTarget(), parent.get(rk.getField()));
            }
            subModel.setStart(0);
            subModel.setLimit(0);
            if(!ValueUtil.isEmpty(sr.getOrderBy())) {
                subModel.setOrderExpr(sr.getOrderBy());
            }
            List list = fetchList(subModel);
            parent.put(sr.getName(), list);
        }
    }

    /**************************************************************************
     * DELETE PROCESS
    ***************************************************************************/
    public void delete(EntityManagerModel entityModel) throws Exception {
        SchemaElement baseElement = entityModel.getElement();
        Map parms = new HashMap();
        parms.putAll(DataTransposer.flatten(entityModel.getFinders(), "_"));
        parms.putAll(entityModel.getWhereParams());
        Map vars = entityModel.getVars();
        SqlDialectModel model = SqlDialectModelBuilder.buildSelectIndexedKeys(entityModel);
        List list = createQuery(model, parms, vars).getResultList();
        for (Object o : list) {
            Map finders = (Map) o;
            deleteOneToMany(entityModel.getSchemaView(), finders);
            deleteSingle(entityModel.getSchemaView(), finders);
        }
    }
    
    private void deleteOneToMany(SchemaView svw, Map finders) throws Exception {
        if( svw.getOneToManyLinks() == null ) return;
        SchemaElement parentElem = svw.getElement();
        for(OneToManyLink oml:  svw.getOneToManyLinks()) {
            SchemaRelation sr = oml.getRelation();
            //check if the linked element has relationships like one to one or one to many
            //we have to load each record in that case.
            Map subFinders = new HashMap();
            for(RelationKey rk: sr.getRelationKeys()) {
                Object val = EntityUtil.getNestedValue(finders, rk.getField());
                subFinders.put( rk.getTarget(), val );
            }
            SchemaElement childElement = sr.getLinkedElement();
            if( childElement.getOneToManyRelationships().size()>0 && childElement.getOneToOneRelationships().size()>0) {
                EntityManagerModel entityModel = new EntityManagerModel(childElement);
                entityModel.getFinders().putAll(subFinders);
                delete(entityModel);
            }
            else {
                //we'll simply delete the record based on its parentid
                EntityManagerModel model = new EntityManagerModel(childElement);
                model.getFinders().putAll(subFinders);
                executeDelete( model );
            }
        };
    }

    private void deleteSingle(SchemaView svw, Map finders) throws Exception {
        Map fieldsToNullify = new HashMap();
        EntityManagerModel model = new EntityManagerModel(svw.getElement());
        
        Map<AbstractSchemaView, EntityManagerModel> toDeleteMap = new LinkedHashMap();
        Map<AbstractSchemaView, EntityManagerModel> toDeleteExtended = new LinkedHashMap();
        for (SchemaViewField vf : svw.getFields()) {
            String n = vf.getExtendedName();
            if ((vf.isPrimary()) && (vf.isBaseField())) {
                model.getFinders().put(n, finders.get(n));
            }
            else if (vf instanceof SchemaViewRelationField) {
                SchemaViewRelationField svf = (SchemaViewRelationField) vf;
                if (svf.getTargetJoinType().equals(JoinTypes.ONE_TO_ONE)) {
                    fieldsToNullify.put(svf.getFieldname(), "{NULL}");

                    AbstractSchemaView tgt = svf.getTargetView();
                    if (!toDeleteMap.containsKey(tgt)) {
                        EntityManagerModel em = new EntityManagerModel(tgt.getElement());
                        toDeleteMap.put(tgt, em);
                    }
                    EntityManagerModel em = (EntityManagerModel) toDeleteMap.get(tgt);
                    Object v = EntityUtil.getNestedValue(finders, svf.getFieldname());
                    em.getFinders().put(svf.getTargetField().getName(), v);
                } 
            }
            else if( vf.isPrimary() && vf.getView().isExtendedView() ) {
                AbstractSchemaView tgtVw = vf.getView();
                if( !toDeleteExtended.containsKey(tgtVw) ) {
                    EntityManagerModel em = new EntityManagerModel(tgtVw.getElement());
                    toDeleteExtended.put( tgtVw, em );
                }
                EntityManagerModel em = (EntityManagerModel) toDeleteExtended.get(tgtVw);
                Object v = EntityUtil.getNestedValue(finders, vf.getExtendedName());
                em.getFinders().put(vf.getExtendedName(), v);
            }
        }
        if (fieldsToNullify.size() > 0) {
            update(model, fieldsToNullify);
        }
        for (EntityManagerModel em : toDeleteMap.values()) {
            executeDelete(em);
        }
        
        executeDelete(model);
        
        for (EntityManagerModel em : toDeleteExtended.values()) {
            executeDelete(em);
        }
    }

    private void executeDelete(EntityManagerModel entityModel) throws Exception {
        SqlDialectModel model = SqlDialectModelBuilder.buildDeleteSqlModel(entityModel);
        executeUpdate(model, entityModel.getFinders(), null);
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /*
    public SqlDialectModel.SubQuery createSubQueryModel(EntityManagerModel model) {
        try {
            SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSelectSqlModel(model);
            Map parms = new HashMap();
            parms.putAll(DataTransposer.flatten(model.getFinders(), "_"));
            parms.putAll(model.getWhereParams());
            SqlDialectModel.SubQuery sq = new SqlDialectModel.SubQuery();
            sq.setSqlModel(sqlModel);
            sq.getParams().putAll(parms);
            return sq;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    */ 
    
    public String buildStatement(EntityManagerModel model) throws Exception {
        SqlDialectModel sqlModel = SqlDialectModelBuilder.buildSelectSqlModel(model);
        return buildStatement(sqlModel);
    }
    
    private void resolveResultForMerge( ComplexField cf, Map data ) {
        if ( cf == null || !cf.isMerge() ) return; 
        if ( data == null || data.isEmpty()) return; 
        if ( !data.containsKey( cf.getName())) return; 
        
        Object info = data.remove( cf.getName()); 
        if ( info instanceof Map ) {
            Map infomap = (Map) info; 
            Map nestedmap = new HashMap();
            extractNestedFields( infomap, nestedmap ); 
            infomap.putAll( nestedmap ); 
            copyItems( data, infomap ); 
            
            data.clear(); 
            data.putAll( infomap ); 
            infomap.clear(); 
        }
    }
    
    private void resolveUpdateForMerge(EntityManagerModel model, Map source ) { 
        ComplexField cxf = model.getElement().findMergeComplexField(); 
        if ( cxf == null || source == null || source.isEmpty() ) return; 
        
        ArrayList<String> sfnames = new ArrayList();
        for( SchemaField sf: model.getElement().getFields() ) { 
            String sname = sf.getName(); 
            sfnames.add( sname ); 
        }
        
        Map info = new HashMap(); 
        Object o = source.remove( cxf.getName()); 
        if ( o instanceof Map ) {
            info = (Map)o; 
        }

        Iterator itr = source.keySet().iterator(); 
        while (itr.hasNext()) {
            Object val = itr.next(); 
            if ( val == null ) continue; 

            String fname = val.toString(); 
            if ( sfnames.contains( fname)) {
                //do nothing 
            } else {
                Object fvalue = source.get( fname ); 
                if ( fvalue instanceof Map ) {
                    if ( info.get( fname ) instanceof Map ) {
                        //do nothing 
                    } else {
                        info.put( fname, new HashMap()); 
                    }
                    copyItems((Map) fvalue, (Map) info.get(fname)); 
                } else { 
                    info.put( fname, source.get( fname )); 
                }
            }
        } 
        
        for( SchemaField sf: model.getElement().getSimpleFields() ) { 
            String sname = sf.getName(); 
            if ( sname.indexOf('_') <= 0 ) continue; 
            
            removeData( info, sname ); 
        }
        
        source.put( cxf.getName(), info ); 
    }
    
    private void removeData( Map data, String name ) {
        if ( data == null || data.isEmpty()) return;
        if ( name == null || name.trim().length() == 0) return;
        
        data.remove( name ); 
        String[] arr = name.split("_"); 
        if ( arr.length > 1 ) {
            Object o = data.get(arr[0]); 
            if ( o instanceof Map ) { 
                Map child = (Map) o; 
                String skey = join(arr, 1, "_"); 
                removeData( child, skey ); 
                if ( child.isEmpty() ) {
                    data.remove( arr[0]); 
                }
            }
        }
    }
    private void copyItems( Map source, Map dest ) { 
        if ( source == null || source.isEmpty()) return;
        
        Iterator keys = source.keySet().iterator(); 
        while (keys.hasNext()) {
            Object okey = keys.next(); 
            if ( okey == null ) continue; 

            String fname = okey.toString(); 
            copyData( fname, source, dest ); 
        } 
    }
    private void copyData( String name, Map source, Map dest ) {
        if ( source == null || source.isEmpty()) return;        
        
        if ( source.containsKey( name ) ) {
            Object o = source.get( name); 
            if ( o instanceof Map ) {
                if ( dest.get(name) instanceof Map ) {
                    //do nothing 
                } else {
                    dest.put(name, new HashMap());
                }
                Map childsrc = (Map) o; 
                Map childdest = (Map) dest.get(name); 
                copyItems( childsrc , childdest );
            } else {
                dest.put( name, source.get( name));  
            }
        } else if ( name.indexOf('_') > 0 ) {
            String[] arr = name.split("_");
            Object o = source.get(arr[0]);
            if ( o instanceof Map ) {
                if ( dest.get(arr[0]) instanceof Map ) { 
                    //do nothing 
                } else { 
                    dest.put(arr[0], new HashMap()); 
                } 
                Map childsrc = (Map) o; 
                Map childdest = (Map) dest.get(arr[0]); 
                String skey = join(arr, 1, "_"); 
                copyData( skey, childsrc, childdest ); 
            }
        }
    }
    private String join( String[] arr, int start, String delimiter ) {
        if ( arr != null && arr.length > 0 && start >= 0 && start < arr.length ) {
            String sdelim = (delimiter == null ? "" : delimiter); 
            StringBuilder sb = new StringBuilder();
            for (int i=start; i<arr.length; i++) {
                if ( sb.length() > 0 ) {
                    sb.append(delimiter); 
                } 
                sb.append( arr[i]); 
            }
            return sb.toString(); 
        } else {
            return null; 
        } 
    }
    private void extractNestedFields( Map source, Map dest ) {
        if ( source == null || source.isEmpty()) return;
        
        ArrayList nestedfields = new ArrayList();
        Iterator keys = source.keySet().iterator(); 
        while (keys.hasNext()) {
            Object okey = keys.next(); 
            if ( okey == null ) continue; 

            String kname = okey.toString().trim(); 
            if ( kname.startsWith("_")) continue;
            
            int idx = kname.indexOf('_'); 
            if (idx <= 0) continue; 
            
            extractNestedField(kname, source.get(okey), dest);
            nestedfields.add( okey ); 
        } 
        
        for ( Object fk : nestedfields ) {
            source.remove( fk ); 
        }
        nestedfields.clear(); 
    }
    private void extractNestedField( String name, Object value, Map dest ) { 
        String[] arr = name.split("_");
        if ( arr.length == 1 ) { 
            dest.put(arr[0], value); 
            return; 
        } 
        
        if ( dest.get(arr[0]) instanceof Map ) { 
            //do nothing  
        } else { 
            dest.put(arr[0], new HashMap()); 
        }
        
        String skey = join(arr, 1, "_"); 
        extractNestedField( skey, value, (Map) dest.get(arr[0])); 
    }
}