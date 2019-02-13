package com.rameses.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.METHOD)
public @interface Schedule {
    int interval() default 0;
    String timeUnit() default "SECONDS";
    boolean fixedInterval() default false;
    boolean immediate() default false;
    String id() default "";
    int index() default 0;
}
