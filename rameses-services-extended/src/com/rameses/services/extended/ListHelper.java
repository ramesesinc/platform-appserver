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
import com.rameses.osiris3.sql.SqlQuery;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class ListHelper {
    
    private EntityManager em;
    private String schemaName;
    private IListListener listener;
    
    private String listMethod = "getList";
    
    public ListHelper(String schemaName, EntityManager em, IListListener listener, String listMethod) {
        this.schemaName = schemaName;
        this.em = em;
        this.listener = listener;
        if(listMethod!=null) {
            this.listMethod = listMethod;
        }
    }
    
    public Object getList(Object data) throws Exception {
        if (!(data instanceof Map))
            throw new RuntimeException("ListHelper.getList parameter must be a Map object");
        
        Map params = (Map)data;
        listener.beforeList(params); 
        if (params.containsKey("searchtext")) {
            String stext = (String) params.get("searchtext");
            if(stext.trim().equals("-")) stext = "";
            stext = stext.trim()+ "%";
            params.put("searchtext", stext+"%" );
        }
        SqlQuery sq = em.getSqlContext().createNamedQuery( schemaName+":"+listMethod );
        sq.setParameters(params).setVars(params);
        
        if (params.containsKey("_start")) {
            int i = Integer.parseInt(params.get("_start")+"");
            sq.setFirstResult( i );
        }
        if (params.containsKey("_limit")) {
            int i = Integer.parseInt(params.get("_limit")+"");
            sq.setMaxResults( i );
        }
        sq.setVars( params );
        
        
        if(params.containsKey("_pagingKeys")) {
            String s = (String)params.get("_pagingKeys");
            String[] arr = s.split(",");
            sq.setPagingKeys( arr );
        }
        
        List list = sq.getResultList();
        listener.afterList(params, list);
        return list;
    }

    
}
