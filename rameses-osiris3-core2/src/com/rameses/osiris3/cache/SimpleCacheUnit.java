/*
 * SimpleCacheUnit.java
 *
 * Created on February 11, 2013, 11:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.cache;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Elmo
 * This is the holder of the actual cache. This is used for the default cache in checking expiry
 */
public class SimpleCacheUnit {
    
    private Date expiry;
    
    //timeout in seconds. default is 30 seconds
    private int timeout;
    private Object value;
    private boolean noExpiry = false;
    
    public SimpleCacheUnit(Object data, int timeout) {
        this.value = data;
        if(timeout<0) {
            noExpiry = true;
        }
        else {
            if(timeout==0) timeout = 30;
            Calendar cal = Calendar.getInstance();
            cal.add( Calendar.SECOND, timeout );
            this.expiry = cal.getTime();
        }
    }

    //if timeout less than 0 it never expires
    public boolean isExpired() {
        if(!noExpiry) {
            Date now = new Date();
            return now.after( expiry );
        }
        else {
            return false;
        }
    }

    public Object getValue() {
        return value;
    }
    
}
