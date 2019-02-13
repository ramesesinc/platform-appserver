/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.schema;

import java.util.Map;
import com.rameses.osiris3.persistence.JoinTypes;

/**
 *
 * @author dell
 * For the base elements, the linked view should be null
 */
public class SchemaViewField {
    
    protected SchemaField schemaField;
    private boolean serialized;
    protected SchemaView rootView;
    protected AbstractSchemaView view;
    
    //underscored field names are calculated during init
    private boolean _insertable;
    private boolean _updatable;
    private String _extendedName;
    private Class _dataTypeClass;
    private String _caption;
    private String joinType;
    
    /**
    * The view here represents the main view that holds the field.
    */
    public SchemaViewField(SchemaField sf, SchemaView rootView, AbstractSchemaView lvw, boolean insertable, boolean updatable) {
        this.schemaField = sf;
        this.rootView = rootView;
        this.view = lvw;
        this._insertable = insertable;
        this._updatable = updatable;
        init();
    }
    
    /**
     * init calculates all the other behaviors or properties of the field.
     */
    private void init() {
        LinkedSchemaView lvw = null;
        joinType = null;
        if( this.view instanceof LinkedSchemaView) {
            lvw = (LinkedSchemaView)this.view;
            joinType = lvw.getJointype();
        }
         //determine extended name
        if(view.equals(rootView) || (joinType!=null && joinType.equals("extended")) ) {
            _extendedName = schemaField.getName();
        }
        else {
            String pfx = (view.getName()!=null) ? view.getName() + "_" : "";
            _extendedName = pfx + schemaField.getName();
        }
        
        
        //determine insertable. applies to extended, one to one 
        /*
        String matchPattern = JoinTypes.EXTENDED + "|" + JoinTypes.ONE_TO_ONE ;
        if( joinType==null || joinType.matches( matchPattern ) ) {
            _insertable = true;
        }
        else if( joinType !=null && joinType.matches(JoinTypes.MANY_TO_ONE)) {
            _insertable = true;
            _updatable = true;
        }
        //determine updatable. Only primary keys will not be updatable. all the rest can be updated
        //if( (joinType==null ||  !joinType.matches(matchPattern)) && !isPrimary() ) {
        if(!isPrimary()) {
            _updatable = true;
        }
        if( schemaField instanceof SimpleField ) {
            if( ((SimpleField)schemaField).getExpr()!=null ) {
                _insertable = false;
                _updatable = false;
            }
        }
        */ 
    }
    
    public String getName() {
        return schemaField.getName();
    }
    
    public String getRef() {
        return null;
    }
    
    public String getFieldname() {
        return schemaField.getFieldname();
    }
    
    public boolean isRequired() {
        return schemaField.isRequired();
    }

    public SchemaElement getElement() {
        return schemaField.getElement();
    }
    
    public Map getProperties() {
        return schemaField.getProperties();
    }

    public Object getProperty(String name) {
        return schemaField.getProperty(name);
    }

    public boolean isSerialized() {
        return serialized;
    }

    public void setSerialized(boolean serialized) {
        this.serialized = serialized;
    }
    
    public SchemaField getSchemaField() {
        return schemaField;
    }

    public AbstractSchemaView getView() {
        return view;
    }
    
    public boolean isPrimary() {
        if(!  (schemaField instanceof SimpleField)) return false;
        SimpleField sf = (SimpleField)schemaField;
        return sf.isPrimary();
    }

    public String getExtendedName() {
        return _extendedName;    
    }

    public boolean isInsertable() {
        return _insertable;
    }

    public boolean isUpdatable() {
        return _updatable;
    }

    public String getExpr() {
        if(!  (schemaField instanceof SimpleField)) return null;
        SimpleField sf = (SimpleField)schemaField;
        return sf.getExpr();
    }
    
    //this will be useful for specifying during building of sql
    public String getTablename() {
        return view.getElement().getTablename();
    }
    
    public String getTablealias() {
        return view.getName();
    }

    //this means the root and the current view is the same
    public boolean isBaseField() {
        return this.rootView.equals(this.view);
    }
    
    public String getCaption() {
        if(_caption == null ) {
            _caption =  (String)schemaField.getProperty("caption");
            _caption = getExtendedName().replace("_", ".");
        }
        return _caption;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name:" + getMapname() + "; ");
        sb.append("extname:" + getExtendedName() + "; ");
        sb.append(" table:" + getTablealias() + ":"+ getTablename());
        sb.append(" dbfield:" + getFieldname() );
        sb.append(" base:"+isBaseField()+ ";"  );
        sb.append(" primary:;"+ isPrimary()+ ";" );
        sb.append( " canInsert:"+ isInsertable()+ ";"  );
        sb.append( " canUpdate:"+isUpdatable()+ ";"  );
        if( isSerialized() ) sb.append( " serialized:yes;"  );
        return sb.toString();
    }

    public String getMapname() {
        return getExtendedName().replace("_", ".");
    }
    
    public String getJoinType() {
        return joinType;
    }
    
    
}
