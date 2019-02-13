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
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class ActiveCrudNodeService extends ActiveCrudListService {
    
    public void beforeNode(Object selectedNode, boolean root) {
        
    }
    public void afterNode(Object selectedNode, Object nodes) {
        
    }
    
    @ProxyMethod
    public Object getNodes( Object node ) throws Exception {
        Map selectedNode  = (Map)node;
        boolean root = false;
        if(selectedNode.containsKey("root")) {
            root = Boolean.parseBoolean(selectedNode.get("root")+"");
        }    
        beforeNode(selectedNode, root);
        List list = null;
        if(root) {
            list = (List) getObj().invokeMethod("getRootNodes", new Object[]{selectedNode}  );
        }
        else {
            list = (List) getObj().invokeMethod("getChildNodes", new Object[]{selectedNode}  );
        }    
        afterNode( selectedNode, list );
        return list;
    }    
    
}
