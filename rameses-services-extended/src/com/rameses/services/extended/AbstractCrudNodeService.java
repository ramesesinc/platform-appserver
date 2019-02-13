/*
 * AbstractCrudNodeService.java
 *
 * Created on August 8, 2013, 8:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.services.extended;

import com.rameses.annotations.ProxyMethod;
import com.rameses.osiris3.persistence.EntityManager;
import com.rameses.osiris3.sql.SqlQuery;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class AbstractCrudNodeService extends AbstractCrudListService {
    
    public void beforeNode(Map selectedNode, boolean root) {
        
    }
    public void afterNode(Map selectedNode, Object nodes) {
        
    }
    
    @ProxyMethod
    public List getNodes( Map selectedNode ) throws Exception {
        boolean root = false;
        if(selectedNode.containsKey("root")) {
            root = Boolean.parseBoolean(selectedNode.get("root")+"");
        }    
        beforeNode(selectedNode, root);
        
        SqlQuery sq = null;
        String _schemaName = getSchemaName();
        if(root) {
            _schemaName += ":getRootNodes";
        }
        else {
            _schemaName += ":getChildNodes";
        }    
        sq = ((EntityManager)getEm()).getSqlContext().createNamedQuery( _schemaName );
        List list = sq.setParameters( selectedNode ).getResultList();
        afterNode( selectedNode, list );
        return list;
    }    
    
}
