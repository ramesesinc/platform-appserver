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
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class ActiveCrudListService extends ActiveCrudService {
    
    public void beforeList(Object data){;}
    public void afterList(Object data, Object list){;}
    public String getPagingKeys() {
        return null;
    }
    
    public String getListMethod() {
        return "getList";
    }

    public boolean isSearchtext() {
        return true;
    }
    
    @ProxyMethod
    public Object getList(Object params) throws Exception {
        Map m = (Map)params;
        if(getPagingKeys()!=null) {
            m.put("_pagingKeys", getPagingKeys());
        }
        
        
        if(isSearchtext()) {
            String searchtext = (String)m.get("searchtext");
            if(searchtext==null) 
                searchtext = "%";
            else {
                if(searchtext.trim().equals("-")) searchtext = "";
                searchtext = searchtext.trim()+ "%";
            }    
            m.put("searchtext", searchtext);
        }

        beforeList(m);
        String listMethod = getListMethod();
        if( m.get("_listMethod")!=null ) {
            listMethod = (String) m.get("_listMethod");
        }
        
        List list = (List) getObj().invokeMethod(listMethod, new Object[]{m});
        afterList(m, list);
        return list;
    }
    
}
