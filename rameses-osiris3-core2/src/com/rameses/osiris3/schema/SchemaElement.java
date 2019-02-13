 /*
 * Schema.java
 *
 * Created on August 12, 2010, 10:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import com.rameses.osiris3.persistence.JoinTypes;
import java.util.HashSet;
import java.util.Set;

public class SchemaElement implements Serializable {
    
    private Schema schema;
    private String name;
    private List<SchemaField> fields = new ArrayList();
    private Map<String,SchemaField> fieldMap = new Hashtable();
    private Map properties = new HashMap();
    
    private List<SimpleField> simpleFields;
    private List<ComplexField> complexFields;
    private List<SimpleField> primaryKeys;
    
    private List<ComplexField> serializedFields;
    
    private SchemaRelation extendedRelationship;
    private List<SchemaRelation> oneToManyRelationships;
    private List<SchemaRelation> oneToOneRelationships;
    private List<SchemaRelation> manyToOneRelationships;
    
    //only one inverse join. reserved for parent
    private List<SchemaRelation> inverseRelationships;
    
    /** Creates a new instance of Schema */
    public SchemaElement(Schema schema) {
        this.schema = schema;
    }
    
    /**
     * method accessed by the parser.
     */
    public void addSchemaField(SchemaField fld) {
        fields.add( fld );
        if( fld.getName()!=null ) {
            fieldMap.put(fld.getName(), fld);
        }  
        fld.setElement(this);
    }
    
    public Map getProperties() {
        return properties;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Schema getSchema() {
        return schema;
    }
    
    public List<SchemaField> getFields() {
        return fields;
    }
    
    
    public SchemaField getField(String name) {
        return fieldMap.get(name);
    }
    
    public List<SimpleField> getSimpleFields() {
        if(simpleFields==null) {
            simpleFields = new ArrayList();
            for( SchemaField sf : getFields() ) {
                if(sf instanceof SimpleField) simpleFields.add((SimpleField) sf);
            }
        }
        return simpleFields;
    }
    
    public List<SimpleField> getPrimaryKeys() {
        if(primaryKeys==null) {
            primaryKeys = new ArrayList();
            for(SimpleField sf: this.getSimpleFields()) {
                if(!sf.isPrimary()) continue;
                primaryKeys.add(sf);
            }
        }
        return primaryKeys;
    }
    
    public List<ComplexField> getComplexFields() {
        if(complexFields==null) {
            complexFields = new ArrayList();
            for( SchemaField sf : getFields() ) {
                if(sf instanceof ComplexField) complexFields.add((ComplexField) sf);
            }
        }
        return complexFields;
    }
    
    public List<ComplexField> getSerializedFields() {
        if(serializedFields==null) {
            serializedFields = new ArrayList();
            for( ComplexField cf : getComplexFields() ) {
                if(cf.getSerializer()==null) continue;
                if(cf.getSerializer().trim().length()==0) continue;
                serializedFields.add(cf);
            }
        }
        return serializedFields;
    }
    
    public ComplexField findMergeComplexField() {
        for (ComplexField cf : getSerializedFields()) {
            if ( cf.isMerge()) return cf; 
        }
        return null; 
    }
    
    public Object getProperty(String name) {
        return this.properties.get( name );
    }
    
    public String getExtends() {
        String ext = (String) this.properties.get("extends");
        if( ext == null ) return null;
        if( ext.indexOf(":")<=0) return ext + ":" + ext;
        return ext;
    }
    
    public String getTablename() {
        return (String) this.properties.get("tablename");
    }
    
    public String getAdapter() {
        return (String) this.properties.get("adapter");
    }
    
    public SchemaElement getExtendedElement() {
        if(this.getExtends()==null) return null;
        return schema.getSchemaManager().getElement(this.getExtends());
    }
    
    /**
     * The schema view represents the complete instance of the schema including 
     * the links and the join tables. 
     * @return 
     */
    private final static Object lock = new Object();
    private SchemaView schemaView;
    public SchemaView createView() {
        synchronized(lock) {
            if( schemaView == null ) {
                schemaView = new SchemaView(this);
                fetchAllFields( schemaView, schemaView, null, true, true, new HashSet(), true, null, null, true );
            }
        }
        return schemaView;
    }
    
    /**
     * 
     * @param rootVw - the main view
     * @param lsvw - the immediate view that relates to the field. 
     */
    //loadOneToMany - if true then loads it. It is false if coming from a many to one recursion
    //manyToOneRequired. 
    //If the source relationship is not required, then automatically cascading elements should also be false.
    //This is to solve the left join problem.
    //includePrimary is false for extended elements. but should be included for many to one views
    private void fetchAllFields(SchemaView rootVw, AbstractSchemaView currentVw, String prefix, 
            boolean insertable, boolean updatable,  Set<SchemaRelation> duplicates, 
            boolean loadOneToMany, String includeFields, Boolean manyToOneRequired, boolean includePrimary) {
        
        for( SimpleField sf: this.getSimpleFields() ) {
            if(sf.isPrimary() && !includePrimary) continue;
            boolean ins = insertable;
            boolean upd = updatable;
            if( sf.isPrimary() ) {
                upd = false;
            } 
            else if(sf.getExpr()!=null && sf.getExpr().trim().length()>0) {
                ins = false;
                upd = false;
            }
            SchemaViewField svf = new SchemaViewField(sf, rootVw, currentVw, ins, upd);
            //System.out.println("compare "+svf.getExtendedName()+" match "+ incFlds);
            if( includeFields==null || svf.getExtendedName().matches(includeFields)) {
                rootVw.addField(svf);
            }
        }
        for( ComplexField cf: this.getSerializedFields() ) {
            SchemaViewField svf = new SchemaViewField(cf, rootVw, currentVw, insertable, updatable);
            //System.out.println("compare "+svf.getExtendedName()+" match "+ incFlds);
            if( includeFields==null || svf.getExtendedName().matches(includeFields)) {
                svf.setSerialized(true);
                rootVw.addField(svf);
            }
        }
        
        SchemaElement extElement = this.getExtendedElement();
        if( extElement!=null ) {
            String n = extElement.getName();
            LinkedSchemaView targetVw = new LinkedSchemaView( n, extElement, rootVw, currentVw, JoinTypes.EXTENDED, true, prefix );
            //loop on primary keys. This assumes that it has the same order as the extended element.
            int isrc = this.getPrimaryKeys().size();
            int itgt = extElement.getPrimaryKeys().size();
            if( isrc!=itgt ) {
                throw new RuntimeException( "Error on fetchAllFields. extends is ignored because the primary keys do not match. " + extElement.getName() );
            }
            for( int i=0; i<isrc; i++) {
                SimpleField _sf = this.getPrimaryKeys().get(i);
                SimpleField _tf = extElement.getPrimaryKeys().get(i);
                SchemaViewRelationField rf = new SchemaViewRelationField(_sf, rootVw, currentVw,insertable, updatable, _tf, targetVw);   
                targetVw.addRelationField(rf);
            }
            currentVw.setExtendsView(targetVw);
            extElement.fetchAllFields(rootVw, targetVw, prefix,true, true, duplicates, true, includeFields, manyToOneRequired, true );
        }
        
        boolean processManyToOne = true; 
        String includeComplex = null; 
        if ( includeFields != null ) {
            StringBuilder sb = new StringBuilder();
            String[] arr = includeFields.split("\\|"); 
            for ( String s : arr ) {                
                if ( s.contains(".")) {
                    String s2 = s.substring(0, s.lastIndexOf('.')); 
                    if ( sb.length() > 0 ) sb.append("|");  
                    
                    sb.append(s2); 
                }
            }
            if ( sb.length() > 0) {
                includeComplex = sb.toString(); 
            } else {
                processManyToOne = false; 
            }
        }
        
        if ( processManyToOne ) {
            List<SchemaRelation> relList = new ArrayList();
            relList.addAll( this.getOneToOneRelationships() );
            relList.addAll( this.getManyToOneRelationships() );

            String[] incflds = (includeComplex != null ? includeComplex.split("\\|") : null); 
            
            //extract all fields related.
            for( SchemaRelation sr: relList  ) { 
                String srname = (prefix==null? "": (prefix+"_")) + sr.getName(); 
                if ( incflds != null && incflds.length > 0 ) { 
                    boolean has_matches = false; 
                    for ( String str : incflds ) {
                        if ( str.equals( srname ) || str.startsWith(srname)) { 
                            has_matches = true;
                            break; 
                        } 
                    } 
                        
                    if ( !has_matches ) continue; 
                } 

                if( duplicates.contains(sr)) continue;
                duplicates.add(sr);

                SchemaElement targetElem = sr.getLinkedElement();
                boolean isRequired = sr.isRequired();
                if(manyToOneRequired!=null) {
                    isRequired = manyToOneRequired.booleanValue();
                }
                LinkedSchemaView targetVw = new LinkedSchemaView(sr.getName(), targetElem, rootVw, currentVw, sr.getJointype(),isRequired, prefix  );
                targetVw.setIncludeFields(sr.getIncludeFields());
                for( RelationKey rk: sr.getRelationKeys() ) {
                    SimpleField tf = (SimpleField)sr.getLinkedElement().getField(rk.getTarget());
                    if( tf == null ) 
                        throw new RuntimeException("SchemaElement.fetchAllFields error. Target field not found for "+ currentVw.getElement().getName()+"."+sr.getName());
                    if(! (tf instanceof SimpleField) ) 
                        throw new RuntimeException("SchemaElement.fetchAllFields error. Target field must be a simple field for "+currentVw.getElement().getName()+"."+sr.getName());

                    //build the simple field
                    SimpleField sf = new SimpleField();
                    sf.setElement(currentVw.getElement());
                    sf.setName(rk.getField());
                    sf.setFieldname(rk.getField());
                    sf.setType( tf.getType() );
                    SchemaViewRelationField rf = new SchemaViewRelationField(sf, rootVw, currentVw, insertable, updatable, tf, targetVw);
                    if( sr.getJointype().equals(JoinTypes.ONE_TO_ONE) ) {
                        currentVw.addOneToOneView( targetVw );
                    }
                    else {
                        currentVw.addManyToOneView( targetVw );
                    }
                    rootVw.addField( rf );
                    targetVw.addRelationField(rf);
                };
                boolean ins = true;
                boolean upd = true;
                if(sr.getJointype().equals(JoinTypes.MANY_TO_ONE)) {
                    ins = false;
                    upd = false;
                }
                Boolean mreq = null;
                if( manyToOneRequired!=null ) {
                    mreq = manyToOneRequired;
                }
                else if( !sr.isRequired() ) {
                    mreq = new Boolean(false);
                }

                //if there are include fields, use this otherwise use the existing.
                String incFlds = includeFields;
                if(incFlds==null) {
                    //build the include fields statement
                    if( sr.getIncludeFields()!=null ) {
                        StringBuilder sb = new StringBuilder();
                        String[] arr = sr.getIncludeFields().split(",");
                        boolean pass = false;
                        for(String s: arr) {
                            if(!pass) pass = true;
                            else sb.append("|");
                            sb.append(sr.getName()+"_");
                            sb.append(s.trim().replace("_", "."));
                        }
                        incFlds = sb.toString();
                    }
                } else {

                }
                targetElem.fetchAllFields(rootVw, targetVw, targetVw.getName(), ins, upd, duplicates, false, incFlds, mreq, true);
            }
        }
        
        //load the one to many relationships
        if(loadOneToMany) {
            for( SchemaRelation sr: this.getOneToManyRelationships() ) {
                rootVw.addOneToManyLink(new OneToManyLink(sr.getName(), prefix, this, sr));
            }
        }
    }    
    
    private void buildRelations(String joinType, List schemaRelations) {
        for(ComplexField cf: this.getComplexFields()) {
            if(cf.getJoinType()==null ) continue;
            if(!cf.getJoinType().toLowerCase().equals(joinType) ) continue;
            String ref = cf.getRef();
            if(ref==null || ref.trim().length()==0) {
                System.out.println("SchemaElement.buildRelations warning." + cf.getName()  + " ref not specified");
                continue;
            }
            SchemaElement elem = this.schema.getSchemaManager().getElement(ref);
            SchemaRelation sr = new SchemaRelation(this, cf);
            sr.setLinkedElement(elem);
            schemaRelations.add(sr);
        }
    }
    
    public List<SchemaRelation> getOneToManyRelationships() {
        if( oneToManyRelationships == null ) {
            oneToManyRelationships = new ArrayList();
            buildRelations( JoinTypes.ONE_TO_MANY, oneToManyRelationships );
        }
        return oneToManyRelationships;
    }
    
    public List<SchemaRelation> getOneToOneRelationships() {
        if( oneToOneRelationships == null ) {
            oneToOneRelationships = new ArrayList();
            buildRelations( JoinTypes.ONE_TO_ONE, oneToOneRelationships );
        }
        return oneToOneRelationships;
    }
    
    public List<SchemaRelation> getManyToOneRelationships() {
        if( manyToOneRelationships == null ) {
            manyToOneRelationships = new ArrayList();
            buildRelations( JoinTypes.MANY_TO_ONE, manyToOneRelationships );
        }
        return manyToOneRelationships;
    }
    
    public boolean hasExtends() {
        return (this.getExtends()!=null && this.getExtends().trim().length()>0);
    }

    
    
}
