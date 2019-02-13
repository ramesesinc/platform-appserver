/*
 * CrudService.java
 *
 * Created on August 5, 2013, 9:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.services.extended;

import com.rameses.annotations.ProxyMethod;
import com.rameses.osiris3.persistence.EntityManager;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class AbstractCrudService implements ICrudListener{
    
    protected abstract Object getEm();
    protected abstract String getSchemaName();
    
    public String getSubSchemaName() {
        return null;
    }
    
    public boolean isValidate() {
        return true;
    }
    
    private CrudHelper getCrudHelper() {
        return new CrudHelper(getSchemaName(), getSubSchemaName(), (EntityManager) getEm(), this, isValidate());
    }
    
    public void beforeCreate(Object data) {;}
    public void afterCreate(Object data) {;}
    public void beforeUpdate(Object data) {;}
    public void afterUpdate(Object data) {;}
    public void beforeOpen(Object data) {;}
    public void afterOpen(Object data) {;}
    public void beforeRemoveEntity(Object data) {;}
    public void afterRemoveEntity(Object data) {;}
    
    @ProxyMethod
    public Object create(Object data) {
        return getCrudHelper().create(data);
    }
    
    @ProxyMethod
    public Object update(Object data) {
        return getCrudHelper().update(data);
    }
    
    @ProxyMethod
    public Object open(Object data) {
        Object d = getCrudHelper().open(data);
        if(d==null || ((Map)d).isEmpty() ) {
            throw new RuntimeException( getSchemaName() + " does not exist" );
        }
        return d;
    }
    
    @ProxyMethod
    public void removeEntity(Object data) {
        getCrudHelper().removeEntity(data);
    }
    
    @ProxyMethod
    public void approve(Object data) {
        getCrudHelper().approve(data);
    }
    
    public void changeState(Object data) {
        getCrudHelper().changeState(data);
    }
    
    
}
