/*
 * FactCollector.java
 *
 * Created on June 10, 2013, 11:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elmo
 * This is a general utility for collecting facts.
 * It includes facility for getting summary, groups, etc.
 */
public class FactList extends AbstractFactList {
    
    private FactFilter filter;
    private List<FactItem> removedFacts = new ArrayList();
    
    public FactList() {
    }
    
    public boolean add(Object fact) {
        if(fact instanceof FactItem ) {
            return add((FactItem)fact);
        }
        if(fact instanceof FactGroup) {
            return super.add( (FactGroup)fact);
        }
        else {
            return add(fact, null);
        }
    }
    
    public boolean add(String id, Object fact, String ruleid, int salience) {
        FactItem newFact = new FactItem(id, fact,ruleid,salience);
        return add(newFact);
    }
    
    public boolean add(Object fact, String ruleid, int salience) {
        FactItem newFact = new FactItem(fact.toString(), fact,ruleid,salience);
        return add(newFact);
    }
    
    public boolean add(Object fact, String ruleid) {
        FactItem newFact = new FactItem(fact.toString(), fact,ruleid,0);
        return add(newFact);
    }
    
    public boolean add(FactItem newFact) {
        if( filter == null ) {
            super.add( newFact );
            return true;
        } 
        else {
            FactItem toAdd = newFact;
            FactItem toRemove = null;
            for(Object o: this ) {
                FactItem f = (FactItem)o;
                int icase = filter.compare( f.getData(), newFact.getData() );
                if(icase == FactFilter.RESUME_NEXT ) {
                    //do nothing
                } else if(icase ==FactFilter.RETAIN_AND_BREAK ) {
                    toAdd = null;
                    break;
                } else if(icase ==FactFilter.LOG_RETAIN_AND_BREAK) {
                    f.setRulenames( toAdd.getRulenames()+","+f.getRulenames() );
                    removedFacts.add( toAdd );
                    toAdd = null;
                    break;
                } else if(icase ==FactFilter.SWAP_AND_BREAK) {
                    toRemove = f;
                    break;
                } else if(icase ==FactFilter.LOG_SWAP_AND_BREAK) {
                    toAdd.setRulenames( f.getRulenames()+","+toAdd.getRulenames() );
                    toRemove = f;
                    removedFacts.add( f );
                    break;
                }
            }
            if(toRemove!=null) remove( toRemove );
            if(toAdd!=null)  {
                super.add( toAdd );
                return true;
            }
        }
        return false;        
    }
    
    public List<FactItem> getRemovedFacts() {
        return removedFacts;
    }
    
    public FactFilter getFilter() {
        return filter;
    }
    
    public void setFilter(FactFilter filter) {
        this.filter = filter;
    }
    
    
}
