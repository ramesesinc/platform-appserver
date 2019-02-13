/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris3.schema;

/**
 *
 * @author dell
 */
public class SchemaViewRelationField extends SchemaViewField {

    private boolean required;
    private AbstractSchemaView targetView;
    private SimpleField targetField;
    
    public SchemaViewRelationField(SimpleField source, SchemaView rootView, AbstractSchemaView view, 
            boolean insertable, boolean updatable, SimpleField targetFld, AbstractSchemaView targetVw) {
        super(source, rootView, view, insertable, updatable );
        this.targetField = targetFld;
        this.targetView = targetVw;
    }
    
    public SimpleField getTargetField() {
        return targetField;
    }
    
    public AbstractSchemaView getTargetView() {
        return targetView;
    }
    
    public String getTargetFieldExtendedName() {
        String prefix =  targetView.getName();
        if( prefix !=null ) prefix += "_";
        return prefix + targetField.getName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( super.toString());
        sb.append(";relation [");
        sb.append("target-alias?" + getTargetView().getName() + "; ");
        sb.append("target-field?" + getTargetField().getFieldname() + "; ");
        sb.append( "]");
        return sb.toString();
    }

    /**
     * If target has a one to one relationship, the field will not be insertable
     * because the assumption is the one to one is same as extended and treated as
     * one single object.
     * @return boolean
     */
    public boolean isInsertable() {
        if( this.targetView instanceof LinkedSchemaView) {
            LinkedSchemaView lsv = (LinkedSchemaView)this.targetView;
            String joinType = lsv.getJointype();
            if( joinType.equals("one-to-one")) {
                return false;
            }
        }
        return true;
    }

    public boolean isUpdatable() {
        return true;
    }
    
    public String getTargetJoinType() {
        LinkedSchemaView lsv = (LinkedSchemaView)this.targetView;
        if(lsv==null) return null;
        return lsv.getJointype();
    }
    
}
