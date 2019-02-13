/*
 * Service.java
 *
 * Created on October 31, 2009, 10:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface Service {
    
    String value() default "";
    String host() default "";
    
    //managed - determines if we should fire the interceptors
    boolean managed() default true;
    boolean async() default false;
    boolean dynamic() default false;
    
    //defined connection in connections folder. This will use this connection
    //to call an external service.
    String connection() default "";
    Class localInterface() default NullIntf.class; 
}
