/*
 * CrudHelper.java
 *
 * Created on August 5, 2013, 12:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.services.extended;

import com.rameses.osiris3.persistence.EntityManager;
import com.rameses.osiris3.sql.SqlExecutor;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class CrudHelper {
    
    private EntityManager em;
    private String schemaName;
    private String mainSchemaName;
    private ICrudListener listener;
    private boolean validate = true;
    
    public CrudHelper(String schemaName, String subSchemaName, EntityManager em, ICrudListener listener, boolean validate) {
        this.schemaName = schemaName;
        this.mainSchemaName = schemaName;
        if(subSchemaName!=null && subSchemaName.trim().length()>0) {
            this.schemaName = this.schemaName+":"+subSchemaName;
        }
        this.em = em;
        this.listener = listener;
        this.validate = validate;
    }
    
    public Object create(Object data) {
        if (!(data instanceof Map))
            throw new RuntimeException("Crud.create parameter must be a Map object");
        
        Map map = (Map)data;
        listener.beforeCreate(map);        
        map = (Map)em.create(schemaName, map);
        listener.afterCreate(map);
        return map;
    }
    
    public Object update(Object data) {
        if (!(data instanceof Map))
            throw new RuntimeException("Crud.update parameter must be a Map object");
        
        Map map = (Map)data;
        listener.beforeUpdate(map);        
        map = (Map)em.update(schemaName, map);
        listener.afterUpdate(map);
        return map;
    }
    
     public Object open(Object data) {
        if (!(data instanceof Map))
            throw new RuntimeException("Crud.open parameter must be a Map object");
        
        Map map = (Map)data;
        listener.beforeOpen(map);
        map = (Map)em.read(schemaName, map); 
        listener.afterOpen(map);
        return map;
    }
     
     public void removeEntity(Object data) {
        if (!(data instanceof Map ))
            throw new RuntimeException("Crud.removeEntity parameter must be a Map object");
        
        Map map = (Map)data;
        listener.beforeRemoveEntity(map);
        em.delete(schemaName, map);
        listener.afterRemoveEntity(map);
    }
    
    public void approve( Object data ) {
        if(! (data instanceof Map ))
            throw new RuntimeException("Crud.approve parameter must be map");
        
        Map map = (Map)data;
        map.put("newstate", "APPROVED");
        changeState(map);
    }
    
    public void changeState( Object data ) {
        if(! (data instanceof Map ))
            throw new RuntimeException("Crud.changeState parameter must be map");
        
        Map map = (Map)data;
        String newState = (String)map.get("newstate");
        String oldState = (String)map.get("oldstate");
        String objid = (String)map.get("objid");
        
        if (newState == null) 
            throw new RuntimeException("Crud.changeState must have a newstate parameter");
        if (objid == null) 
            throw new RuntimeException("Crud.changeState must have an objid parameter");

        try {
            String dbName = mainSchemaName + ":changeState-" + newState.toLowerCase();
            SqlExecutor sqe = em.getSqlContext().createNamedExecutor(dbName).setParameters(map);
            int result = (Integer)sqe.execute();
            if (result == 0) {
                throw new Exception("Record change state was unsuccessful. Current state is incorrect");
            }
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
}
