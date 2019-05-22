package com.jy.medusa.validator.annotation;

import java.lang.annotation.*;

/**
 * Author neo 2016.07.12
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Valid {

    Class<? extends Object> entityClass() default Object.class;
}

