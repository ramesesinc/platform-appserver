/*
 * EffectiveDate.java
 *
 * Created on October 22, 2013, 1:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rules.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Elmo
 */
public class EffectiveDate {
    
    private Date date;
    private int numericDate;
    private SimpleDateFormat dformat = new SimpleDateFormat("yyyyMMdd");
    
    /** Creates a new instance of EffectiveDate */
    public EffectiveDate(Date date) {
        this.date = date;
        numericDate = Integer.parseInt(dformat.format(date));
    }

    public Date getDate() {
        return date;
    }
    
    public int getNumericDate() {
        return this.numericDate;
    }
    
}
