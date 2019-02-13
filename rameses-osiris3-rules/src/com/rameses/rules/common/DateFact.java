/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rules.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author dell
 */
public class DateFact {
    
    private Date date;
    private int year;
    private int qtr = 0;
    private int month;
    private int hour;
    private int minute;
    private int second;
    private int milliSecond;
    private int day;
    private int dayOfWeek;  //Mon,Tue,Wed,Thu,Fri
    private Date monthEnd;
    private Date monthBegin;
    private Date timeStamp;    //date without 
    
    /** Creates a new instance of CurrentDate */
    public DateFact() {
        parseDate( new Date());
    }
    
    /** Creates a new instance of CurrentDate */
    //normally passed without time
    public DateFact( String d ) {
        parseDate( formatDate(d, null) );
    }
    
    public DateFact(String d, String pattern) {
        parseDate( formatDate(d, pattern) );
    }
    
    public DateFact(Date d) {
        parseDate(d);
    }
    
    private Date formatDate(String d, String pattern) {
        if(pattern==null) pattern = "yyyy-MM-dd";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(d);
        }
        catch(Exception ex) {
            throw new RuntimeException("DateFact error:" + ex.getMessage());
        }
    }
    
    
    private void parseDate(Date d) {
        this.timeStamp = d;
        Calendar cal = Calendar.getInstance();
        cal.setTime( this.timeStamp  );
        month = cal.get( Calendar.MONTH ) + 1;
        day = cal.get( Calendar.DATE );
        year = cal.get( Calendar.YEAR );
        hour = cal.get( Calendar.HOUR_OF_DAY );
        minute = cal.get( Calendar.MINUTE );
        second = cal.get( Calendar.SECOND );
        milliSecond = cal.get( Calendar.MILLISECOND );
        if( month >= 1 && month <= 3 ) qtr = 1;
	else if( month >= 4 && month <= 6 ) qtr = 2;
	else if( month >= 7 && month <= 9 ) qtr = 3;
        else qtr = 4;
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        cal.set( Calendar.HOUR_OF_DAY, 0  );
        cal.set( Calendar.MINUTE, 0  );
        cal.set( Calendar.SECOND, 0  );
        cal.set( Calendar.MILLISECOND, 0  );
        this.date = cal.getTime();
        
        //set begin date
        cal.set( Calendar.DAY_OF_MONTH, 1 );
        monthBegin = cal.getTime();
        
        //calculate month end
        int ds = cal.getActualMaximum( Calendar.DAY_OF_MONTH );
        cal.set( Calendar.DAY_OF_MONTH, ds );
        monthEnd = cal.getTime();
        
    }

    public Date getDate() {
        return date;
    }

    public int getYear() {
        return year;
    }

    public int getQtr() {
        return qtr;
    }

    public int getMonth() {
        return month;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getDay() {
        return day;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public Date getMonthEnd() {
        return monthEnd;
    }
    
    public Date getMonthBegin() {
        return monthBegin;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public int getMilliSecond() {
        return milliSecond;
    }
    
}
