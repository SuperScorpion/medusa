package com.jy.medusa.validator.annotation;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Length {
  
  public int max() default Integer.MAX_VALUE;
  
  public int min() default 0;
}

