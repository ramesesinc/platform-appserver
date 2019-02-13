/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author dell
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.METHOD)
public @interface Shutdown {
    int index() default 0;
}
