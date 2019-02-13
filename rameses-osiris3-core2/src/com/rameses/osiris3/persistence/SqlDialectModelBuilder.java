/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.persistence;

import com.rameses.osiris3.schema.JoinLink;
import com.rameses.osiris3.persistence.EntityManagerModel.WhereElement;
import com.rameses.osiris3.persistence.SelectFieldsTokenizer.Token;
import com.rameses.osiris3.schema.AbstractSchemaView;
import com.rameses.osiris3.schema.LinkedSchemaView;
import com.rameses.osiris3.schema.RelationKey;
import com.rameses.osiris3.schema.SchemaView;
import com.rameses.osiris3.schema.SchemaViewField;
import com.rameses.osiris3.schema.SchemaViewRelationField;
import com.rameses.osiris3.schema.SimpleField;
import com.rameses.osiris3.sql.SqlDialectModel;
import com.rameses.osiris3.sql.SqlDialectModel.Field;
import com.rameses.osiris3.sql.SqlDialectModel.WhereFilter;
import com.rameses.osiris3.sql.SqlExprParserUtil;
import com.rameses.util.EntityUtil;
import com.rameses.util.ValueUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
public final class SqlDialectModelBuilder {

    /**
     * ***********************************************************************
     * BUILDER HELPERS
     * ***********************************************************************
     */
      
    private static String correctExpr(String expr) {
        return expr.replaceAll("\\s{1,}", " ").replaceAll("\\s{1,}(?=[,|\\(|\\)])", "").replaceAll("(?<=[,|\\(|\\)])\\s{1,}", "");
    }


    /**
     * ***********************************************************************
     * CREATE
     * ***********************************************************************
     */
    public static void buildCreateSqlModels(SchemaView svw, final Map<String, SqlDialectModel> sqlModelMap) {
        //basic create statement
        
        for( SchemaViewField vf:  svw.getFields() ) {
            //System.out.println("field for insert ->" + vf.getName() + ":vwname: "+vf.getView().getName()+ ":: insertable?" + vf.isInsertable() + " element name:"+vf.getElement().getName());
            if (!vf.isInsertable()) continue;
            AbstractSchemaView vw = vf.getView();
            SqlDialectModel model = sqlModelMap.get(vw.getName());
            if (model == null) {
                model = new SqlDialectModel();
                model.setAction("create");
                model.setTablealias(vw.getName());
                model.setTablename(vw.getTablename());
                //model.setSchemaView(vw);
                sqlModelMap.put(vw.getName(), model);
            }
            model.addField(createSqlField(vf));
        }
    }

    private static SqlDialectModel.Field createSqlField(SchemaViewField vf) {
        SqlDialectModel.Field f = new SqlDialectModel.Field();
        f.setName( vf.getName() );
        f.setTablename(vf.getTablename());
        f.setTablealias(vf.getTablealias());
        f.setExtendedName(vf.getExtendedName());
        f.setFieldname(vf.getFieldname());
        f.setPrimary(vf.isPrimary());
        f.setInsertable(vf.isInsertable());
        f.setUpdatable(vf.isUpdatable());
        f.setSerialized(vf.isSerialized());
        f.setBasefield(vf.isBaseField());
        f.setExpr(vf.getExpr());
        return f;
    }
    
    public static SqlDialectModel buildOneToOneUpdateLinkedId(final AbstractSchemaView targetVw) {
        SchemaView svw = targetVw.getRootView();
        SqlDialectModel model = new SqlDialectModel();
        model.setAction("update");
        model.setTablealias(targetVw.getParent().getName());
        model.setTablename(targetVw.getParent().getTablename());
        model.addJoinedViews(targetVw.getParent().getJoinPaths());

        for( SchemaViewField vf: svw.getFields() ) {
            if (vf.isBaseField() && vf.isPrimary()) {
                model.addFinderField(createSqlField(vf));
            }
            if (!(vf instanceof SchemaViewRelationField)) continue;
            SchemaViewRelationField svrf = (SchemaViewRelationField) vf;
            if (!svrf.getTargetJoinType().equals(JoinTypes.ONE_TO_ONE)) continue;
            
            //add the field to update
            AbstractSchemaView tvw = svrf.getTargetView();
            if (!tvw.equals(targetVw)) continue;
            Field f = createSqlField(vf);
            model.addField(f);
        }
        return model;
    }

    /**
     * ***********************************************************************
     * UPDATE
     * ***********************************************************************
     */
    public static Map<String, SqlDialectModel> buildUpdateSqlModels(EntityManagerModel entityModel, final Map data) {
        SchemaView svw = entityModel.getSchemaView();
        String fieldMatch =  DataUtil.stringifyMapKeys(data);
        if(fieldMatch==null) 
            throw new RuntimeException("Update error. There should be at least one field to update");
        //build the where
        
        Map<String, SqlDialectModel> modelMap = new HashMap();
        //build update map fields
        for(SchemaViewField vf: svw.getFields()) {
            if(!vf.isUpdatable()) continue;
            String extName = vf.getExtendedName();
            if( !extName.matches(fieldMatch) ) continue;
            AbstractSchemaView avw = svw;
            if (vf.getView() != null) {
                avw = vf.getView();
            }
            SqlDialectModel sqlModel = modelMap.get(avw.getName());
            if (sqlModel == null) {
                sqlModel = new SqlDialectModel();
                //sqlModel.setSchemaView(svw);
                sqlModel.setAction("update");
                sqlModel.setTablename(avw.getElement().getTablename());
                sqlModel.setTablealias(avw.getElement().getName());
                sqlModel.addJoinedViews(avw.getJoinPaths());
                modelMap.put(avw.getName(), sqlModel);
            }
            sqlModel = modelMap.get(avw.getName());
            //check the data if it is an expression
            SqlDialectModel.Field sqlF = createSqlField(vf);
            try {
                Object val = EntityUtil.getNestedValue(data, extName);
                if(val!=null && (val instanceof String)) {
                    String t = val.toString().trim();
                    if(t.startsWith("{") && t.endsWith("}")) {
                        String texpr = t.substring(1, t.length()-1);
                        String sexpr = parseFieldExpression(texpr, entityModel, sqlModel, svw );
                        sqlF.setExpr( sexpr );
                    }
                }
            }
            catch(Exception ign){;}
            sqlModel.addField(sqlF);
        }
        
        //attach the where and finders in each sql model
        for (SqlDialectModel sqlModel : modelMap.values()) {
            //build the finders if any
            addFinders(entityModel, sqlModel, svw);
            addWhereCriteria( entityModel, sqlModel, svw );
        }
        return modelMap;
    }
    
    /**************************************************************************
     * DELETE STATEMENTS 
     * This is only applicable to primary keys and nothing else. We cannot use
     * where statements because it might produce an error due to the extended
     * tables
    ****************************************************************************/
    public static SqlDialectModel buildDeleteSqlModel(EntityManagerModel entityModel) {
        SchemaView svw = entityModel.getSchemaView();
        SqlDialectModel sqlModel = new SqlDialectModel();
        sqlModel.setAction("delete");
        sqlModel.setTablename(svw.getTablename());
        sqlModel.setTablealias(svw.getName());
        addFinders(entityModel, sqlModel, svw);
        addWhereCriteria(entityModel, sqlModel, svw);
        return sqlModel;
    }
    
    public static Collection<SqlDialectModel> buildDeleteSqlModels1(EntityManagerModel entityModel) {
        SchemaView svw = entityModel.getSchemaView();
        //we use linked hash map to ensure the order of deletes
        Map<String, SqlDialectModel> map = new LinkedHashMap();
        for( SchemaViewField vf: svw.getFields() ) {
            if( vf.isPrimary() ) {
                AbstractSchemaView vw = vf.getView();
                LinkedSchemaView lvw = null;
                if( vw instanceof LinkedSchemaView ) {
                    lvw = (LinkedSchemaView)vw;
                    if(!lvw.getJointype().matches(JoinTypes.ONE_TO_ONE+"|"+JoinTypes.EXTENDED)) continue;
                }
                if(!map.containsKey(vw.getName())) {
                    SqlDialectModel sqlModel = new SqlDialectModel();
                    sqlModel.setAction("delete");
                    sqlModel.setTablename(vw.getTablename());
                    sqlModel.setTablealias(vw.getName());
                    map.put(vw.getName(), sqlModel);
                }
                SqlDialectModel sqlModel = map.get(vw.getName());
                if( lvw ==null || lvw.getJointype().equals(JoinTypes.EXTENDED)) {
                    sqlModel.addFinderField(createSqlField(vf));
                }
                else {
                    addFinders(entityModel, sqlModel, svw);
                    addWhereCriteria(entityModel, sqlModel, svw);
                }
            }
        }
        return map.values();
    }

    public static Map<String, SqlDialectModel> buildNullifyOneToOneLinks(EntityManagerModel model) {
        SchemaView svw = model.getSchemaView();
        Map<String, SqlDialectModel> map = new HashMap();
        for( SchemaViewField vf: svw.getFields() ) {
            if( vf instanceof SchemaViewRelationField  ) {
                SchemaViewRelationField svf = (SchemaViewRelationField)vf;
                if(!svf.getTargetJoinType().equals(JoinTypes.ONE_TO_ONE)) continue;
                AbstractSchemaView vw = vf.getView();
                if(!map.containsKey(vw.getName())) {
                    SqlDialectModel sqlModel = new SqlDialectModel();
                    sqlModel.setAction("update");
                    sqlModel.setTablename(vw.getTablename());
                    sqlModel.setTablealias(vw.getName());
                    addFinders( model,sqlModel,svw );
                    addWhereCriteria( model,sqlModel,svw );
                    map.put(vw.getName(), sqlModel);
                };
                SqlDialectModel sqlModel = map.get(vw.getName());
                SqlDialectModel.Field f = createSqlField(vf);
                f.setExpr("NULL");
                sqlModel.addField(f);
            }
        }
        return map;
    }
    
    /**
     * This selects primary keys as well as other keys in one to one, many-to-one
     * keys
     * @param entityModel
     * @return 
     */
    public static SqlDialectModel buildSelectIndexedKeys(EntityManagerModel entityModel) {
        SchemaView svw = entityModel.getSchemaView();
        SqlDialectModel sqlModel = new SqlDialectModel();
        sqlModel.setAction("select");
        sqlModel.setTablename(svw.getTablename());
        sqlModel.setTablealias(svw.getName());
        for( SchemaViewField vf: svw.getFields() ) {
            if(  vf.isPrimary() && vf.isBaseField() ) {
                sqlModel.addField(createSqlField(vf));
                sqlModel.addJoinedViews( vf.getView().getJoinPaths() );
            }
            else if( vf instanceof SchemaViewRelationField ) {
                SchemaViewRelationField svf = (SchemaViewRelationField)vf;
                if( svf.getTargetJoinType().equals(JoinTypes.ONE_TO_ONE) ) {
                    sqlModel.addField(createSqlField(vf));
                }
            }
        }
        addFinders(entityModel, sqlModel, svw);
        addWhereCriteria(entityModel, sqlModel, svw);
        return sqlModel;
    }

     /**
     * **********************************************************************
     * SELECT
    ***********************************************************************
     /*
     * This function parses the expression for fields. It also returns the 
     * corrected expression. 
     */ 
    public static String parseFieldExpression(String expr, ISelectModel entityModel, SqlDialectModel sqlModel, SchemaView svw ) {
        InputStream is = null;
        try {
            StringBuilder sb = new StringBuilder();
            is = new ByteArrayInputStream(expr.getBytes());
            StreamTokenizer st = SqlExprParserUtil.createStreamTokenizer(is);
            int i = 0;
            while ((i = st.nextToken()) != st.TT_EOF) {                
                if (i == st.TT_WORD) {
                    String v = st.sval.replace(".","_");
                    SchemaViewField vf = svw.getField(v);
                    if (vf != null) {
                        sqlModel.addExprField( createSqlField(vf) );
                        sqlModel.addJoinedViews( vf.getView().getJoinPaths() );
                        sb.append( v );
                        continue;
                    }
                    
                    //if field not exist in basic it might exist in the dialect model's subqueries
                    if( st.sval.indexOf(".")>0 ) {
                        String fname = st.sval;
                        String prefix = fname.substring(0, fname.indexOf("."));
                        fname = fname.substring(fname.indexOf(".")+1).replace(".", "_");

                        //check is in subquery or is in inverse views?
                        SqlDialectModel sqm = sqlModel.getSubqueries().get(prefix);
                        if( sqm !=null ) {
                            Field f = sqm.getSelectField(fname);
                            if( f !=null ) {
                                sb.append( prefix + "." + fname );
                            }
                            continue;
                        }
                        //check if in abstract schema view
                        LinkedSchemaView ivw = (LinkedSchemaView) findJoinedLinkedView(prefix, entityModel, svw, sqlModel);
                        if( ivw !=null ) {
                            SchemaViewField lf = ivw.getElement().createView().getField(fname);
                            Field ff = createSqlField(lf);
                            ff.setExtendedName(prefix+"_"+fname);
                            ff.setTablealias(prefix);
                            sqlModel.addExprField( ff );
                            sqlModel.addJoinedViews( lf.getView().getJoinPaths() );
                            sb.append( prefix + "." + fname );
                            continue;
                        }
                    }
                    
                    //print if not handled 
                    sb.append( st.sval );
                }
                else if( i == '\'') {
                    sb.append( "'" + st.sval + "'" );
                }
                else {
                    sb.append( (char)i );
                }
            }
            return sb.toString();
        } 
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        finally {
            try { is.close(); } catch (Exception e) {;}
        }
    }
    
    /****
     * we will also check the values inside the params because there might be expressions also 
     */ 
    public static WhereFilter createWhereFilter( WhereElement we, ISelectModel entityModel, SqlDialectModel sqlModel, SchemaView svw  ) {
        String sExpr = parseFieldExpression( we.getExpr(),entityModel, sqlModel, svw );
        WhereFilter wf = new WhereFilter(sExpr);
        return wf;
    }
    
    public static void addFinders( ISelectModel entityModel, SqlDialectModel sqlModel, SchemaView svw  ) {
        if( entityModel.getFinders()!=null ) {
            Map finders = entityModel.getFinders();
            for( Object k: finders.keySet() ) {
                String s = k.toString().trim().replace(".","_");
                SchemaViewField vf = svw.getField(s);
                if( vf == null ) throw new RuntimeException("Finder field " + s + " does not exist" );
                SqlDialectModel.Field sf = createSqlField(vf);
                Object val = finders.get(k);
                if( val !=null && (val instanceof SubQueryModel) ) {
                    SubQueryModel sqm = (SubQueryModel)val;
                    SqlDialectModel subQryModel = buildSubQueryModel( sqm, sqlModel, svw);
                    sf.setSubQuery(subQryModel);
                }
                sqlModel.addFinderField( sf );
                sqlModel.addJoinedViews( vf.getView().getJoinPaths() );
            }
        }
    }
    
    public static void addWhereCriteria( ISelectModel entityModel, SqlDialectModel sqlModel,  SchemaView svw  ) {
        if( entityModel.getWhereElement()!=null ) {
            WhereFilter wf = createWhereFilter(entityModel.getWhereElement(), entityModel, sqlModel, svw);
            sqlModel.setWhereFilter(wf);
        }
        if( entityModel.getOrWhereList()!=null && entityModel.getOrWhereList().size()>0) {
            for(WhereElement we: entityModel.getOrWhereList() ) {
                WhereFilter wf = createWhereFilter(we, entityModel, sqlModel, svw);
                sqlModel.addOrWhereFilter(wf);
            }
        }
    }

    public static SqlDialectModel buildSubQueryModel( SubQueryModel subQryModel, SqlDialectModel sqlModel, SchemaView svw ) {
        SqlDialectModel subQuery = buildSelectSqlModel(subQryModel);
        subQuery.setJoinType(subQryModel.getJointype());
        //find the keys 
        for( RelationKey rk: subQryModel.getRelationKeys() ) {
            String extName = rk.getField().replace(".", "_");
            SchemaViewField vf = svw.getField(extName);
            if( vf == null ) 
                throw new RuntimeException("Error buildSubQueryModel. field "+rk.getField()+" not found in parent");
            //this field is on the main side (parent). add join paths in case the linked field is not selected
            SqlDialectModel.Field src = createSqlField(vf);
            sqlModel.addJoinedViews(vf.getView().getJoinPaths());
            
            String textName = rk.getTarget().replace(".","_");
            SqlDialectModel.Field tgt = subQuery.getSelectField(textName);
            if(tgt==null) 
                throw new RuntimeException("Error buildSubQueryModel. sub query field "+rk.getTarget()+" not found in parent");
            //create 
            Field newfld = new Field();
            newfld.setExtendedName(tgt.getExtendedName());
            newfld.setFieldname(tgt.getExtendedName());
            newfld.setTablealias(subQryModel.getName());
            newfld.setTablename(subQryModel.getName());
            subQuery.addRelationKey(src, newfld);
        }
        return subQuery;
    }
    
    public static void buildSubQueryModels( ISelectModel entityModel, SqlDialectModel sqlModel, SchemaView svw ) {
        if( entityModel.getSubqueries()==null || entityModel.getSubqueries().size()<=0 ) return;
        for( Object m: entityModel.getSubqueries().entrySet() ) {
            Map.Entry<String, SubQueryModel> me = (Map.Entry)m;
            SqlDialectModel subQuery = buildSubQueryModel(me.getValue(), sqlModel, svw);
            sqlModel.addSubQuery(me.getKey(), subQuery);
        }
    }
    
    public static SqlDialectModel.Field createSubQueryField(SchemaViewField svf, String subQryAlias) {
        Field newfld = new Field();
        newfld.setExtendedName(subQryAlias + "_" + svf.getExtendedName());
        newfld.setFieldname(svf.getExtendedName());
        newfld.setName(svf.getExtendedName());
        newfld.setTablealias(subQryAlias);
        newfld.setTablename(subQryAlias);
        return newfld;
    }
    
    private static AbstractSchemaView findJoinedLinkedView(String name, ISelectModel entityModel, SchemaView svw, SqlDialectModel sqlModel) {
        JoinLink joinLink = null;
        for(JoinLink jl: entityModel.getJoinLinks()) {
            if(jl.getName().equals(name)) {
                joinLink = jl;
                break;
            }
        }
        if(joinLink == null) return null;
        //check if registered in sqlModel
        AbstractSchemaView lvw = sqlModel.findJoinedView(name);
        LinkedSchemaView targetVw = null; 
        if( lvw == null || !(lvw instanceof LinkedSchemaView) ) {
            targetVw = new LinkedSchemaView(joinLink.getName(), joinLink.getElement(), svw, svw,JoinTypes.MANY_TO_ONE, joinLink.isRequired(), null);
            for( RelationKey rk: joinLink.getRelationKeys()) {
                SchemaViewField tvf = joinLink.getElement().createView().getField(rk.getTarget());
                if( tvf == null ) 
                    throw new RuntimeException("SchemaElement.buildJoins error. Target field not found");
                SimpleField tf = new SimpleField();
                tf.setElement(targetVw.getElement());
                tf.setName(tvf.getExtendedName());
                tf.setFieldname(tvf.getFieldname());
                sqlModel.addJoinedView(targetVw);
                sqlModel.addJoinedViews(tvf.getView().getJoinPaths());
                
                //build the simple field
                SimpleField sf = new SimpleField();
                sf.setElement(svw.getElement());
                sf.setName(rk.getField());
                sf.setFieldname(rk.getField());
                sf.setType( tf.getType() );
                SchemaViewRelationField rf = new SchemaViewRelationField(sf, svw, svw,false, false, tf, targetVw);
                targetVw.addRelationField(rf);
            };
        }
        return targetVw;
    }
    
    private static void buildGroupBy( ISelectModel entityModel, SqlDialectModel sqlModel, SchemaView svw  ) {
        if( entityModel.getGroupByExpr()!=null) {
            List<Token> tokenList = SelectFieldsTokenizer.tokenize(entityModel.getGroupByExpr());
            for( Token t: tokenList ) {
                if( !t.hasExpr() ) {
                    SchemaViewField vf = svw.getField(t.getFieldMatch());
                    sqlModel.addGroupField( createSqlField(vf) );
                    sqlModel.addJoinedViews( vf.getView().getJoinPaths() );
                }
                else {
                    String expr = parseFieldExpression( t.getExpr(), entityModel, sqlModel, svw );
                    SqlDialectModel.Field sf = new SqlDialectModel.Field();
                    sf.setExpr(expr);
                    sqlModel.addGroupField(sf);    
                }
            }
        }     
    }
    
    private static void buildOrderBy( ISelectModel entityModel, SqlDialectModel sqlModel, SchemaView svw  ) {
        if( entityModel.getOrderExpr()!=null) {
            List<Token> tokenList = SelectFieldsTokenizer.tokenize(entityModel.getOrderExpr());
            for( Token t: tokenList ) {
                if( !t.hasExpr() ) {
                    SchemaViewField vf = svw.getField(t.getFieldMatch());
                    if( vf == null ) {
                        System.out.println("warning buildOrderBy. field has no match for : "+t.getFieldMatch());
                        continue;
                    }
                    SqlDialectModel.Field ordf = createSqlField(vf);
                    ordf.setSortDirection(t.getSortDirection());
                    sqlModel.addOrderField( ordf );
                    sqlModel.addJoinedViews( vf.getView().getJoinPaths() );
                }
                else {
                    String expr = parseFieldExpression( t.getExpr(),entityModel, sqlModel, svw );
                    SqlDialectModel.Field sf = new SqlDialectModel.Field();
                    sf.setExpr(expr);
                    sqlModel.addOrderField(sf);    
                }
            }
        }
        
    }
    
    //for research
    public static SqlDialectModel buildSelectSqlModel( ISelectModel entityModel ) {
        SchemaView svw = entityModel.getSchemaView();
        
        SqlDialectModel sqlModel = new SqlDialectModel();
        sqlModel.setAction("select");
        sqlModel.setTablename(svw.getTablename());
        sqlModel.setTablealias(svw.getName());
        
        //if there are subqueries, we must build it first before doing any parsing. 
        //THIS MUST COME FIRST.
        buildSubQueryModels(entityModel, sqlModel, svw);
        
        //tokenize each field, then find out which fields will be considered in the select
        List<Token> fieldMatchList = SelectFieldsTokenizer.tokenize(entityModel.getSelectFields());
        for(Token t: fieldMatchList ) {
            if( !t.hasExpr()) {
                boolean consumed = false;
                for(SchemaViewField vf: svw.findAllFields()) {
                    if(vf.getExtendedName().matches(t.getFieldMatch())) {
                        SqlDialectModel.Field sf = createSqlField(vf);
                        sqlModel.addField(sf);
                        //handle fields with expressions
                        if( !ValueUtil.isEmpty(vf.getExpr())) {
                            String expr = parseFieldExpression( vf.getExpr(), entityModel, sqlModel, svw );
                            sf.setExpr(expr);
                        }
                        else {
                            sqlModel.addJoinedViews( vf.getView().getJoinPaths() );
                        }    
                        consumed = true;
                    }
                };
                //if token field does not have a match, try to look in the subqueries
                //The requirement is there must be a prefix, otherwise we will ignore it.
                if( !consumed ) {
                    //remove the first part of prefix:
                    int idx = t.getFieldMatch().indexOf(".");
                    if( idx > 0 ) {
                        String prefix = t.getFieldMatch().substring(0, idx);
                        String fname = t.getFieldMatch().substring(idx+1);
                        if(fname.equals("*")) fname = ".*";
                        
                        //check if it exists in sub query
                        SqlDialectModel subQryModel = sqlModel.getSubqueries().get(prefix);
                        if( subQryModel !=null ) {
                            List<SqlDialectModel.Field> subQryFlds = sqlModel.findAllSubQueryFields( prefix, fname );
                            if( subQryFlds != null ) {
                                for( SqlDialectModel.Field f: subQryFlds ) {
                                    sqlModel.addField(f);
                                }
                            }
                            continue;
                        };
                        
                        //check if field exists in the inverse joins. add it before loading
                        LinkedSchemaView lsv = (LinkedSchemaView) findJoinedLinkedView(prefix, entityModel, svw, sqlModel);
                        //find fields for selection
                        if( lsv !=null) {
                            for( SchemaViewField xvf: lsv.getElement().createView().findAllFields() ) {
                                if(xvf.getExtendedName().matches(fname)) {
                                    SqlDialectModel.Field sf = createSqlField(xvf);
                                    sf.setTablealias(lsv.getName());
                                    sf.setExtendedName(lsv.getName()+"_"+fname);
                                    sqlModel.addField(sf);
                                    sqlModel.addJoinedViews( xvf.getView().getJoinPaths() );
                                }
                            }
                        }
                    }
                }
            }
            else {
                String expr = parseFieldExpression( t.getExpr(), entityModel, sqlModel, svw );
                SqlDialectModel.Field sf = new SqlDialectModel.Field();
                sf.setExtendedName(t.getAlias());
                sf.setExpr(expr);
                sqlModel.addField(sf);
            }
        };
        
        //build the finders if any. we'll try to put this first bec. there's a problem in select fields.
        addFinders(entityModel, sqlModel, svw);
        addWhereCriteria( entityModel, sqlModel, svw );
        buildGroupBy( entityModel, sqlModel, svw );
        buildOrderBy( entityModel, sqlModel, svw );
        
        sqlModel.setStart(entityModel.getStart());
        sqlModel.setLimit(entityModel.getLimit());
        return sqlModel;
    }
    
    
    
    
}
