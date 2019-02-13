/*
 * AbstractCrudListService.java
 *
 * Created on August 5, 2013, 12:26 PM
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
public abstract class AbstractCrudListService extends AbstractCrudService  implements IListListener{
    
    public void beforeList(Object data){;}
    public void afterList(Object data, Object list){;}
    
    public String getListMethod() {
        return "getList";
    }
    
    public String getPagingKeys() {
        return null;
    }
    
    private ListHelper getListHelper() {
        return new ListHelper(getSchemaName(), (EntityManager) getEm(), this, this.getListMethod());
    }
    
    @ProxyMethod
    public Object getList(Object params) {
        try {
            if(getPagingKeys()!=null) {
                ((Map)params).put("_pagingKeys", getPagingKeys());
            }
            return getListHelper().getList( params );
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception ex){
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
}
