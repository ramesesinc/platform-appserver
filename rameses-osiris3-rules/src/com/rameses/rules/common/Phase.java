/*
 * PhaseList.java
 *
 * Created on June 13, 2013, 3:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Elmo
 */
public class Phase {
    
    private PhaseEntry currentPhase;
    private List<PhaseEntry> list = new ArrayList();
    private Iterator<PhaseEntry> iterator;
    private boolean cancelled;
    private boolean hasMore;
    private boolean started;
    
    /** Creates a new instance of PhaseList */
    public Phase() {
        
    }
    
    public void addPhase(String name, String category) {
        addPhase(new PhaseEntry(name, category));
    }
    
    public void addPhase(PhaseEntry ph) {
        list.add(ph);
    }
    
    public boolean start() {
        if(list.size()==0)
            return false;
        started = true;
        iterator = list.iterator();
        nextPhase();
        return started;
    }
    
    public PhaseEntry nextPhase() {
        hasMore = iterator.hasNext();
        if(!hasMore) return null;
        currentPhase = iterator.next();
        return currentPhase;
    }

    public PhaseEntry getCurrentPhase() {
        return currentPhase;
    }
     public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
    
    public String getName() {
        if(currentPhase!=null)return currentPhase.getName();
        return null;
    }
    
    public String getCategory() {
        if(currentPhase!=null)return currentPhase.getCategory();
        return null;
    }

    public boolean isStarted() {
        return started;
    }

}
