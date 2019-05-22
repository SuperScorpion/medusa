package com.jy.medusa.validator.annotation;

import java.lang.annotation.*;

/**
 * Author neo 2016.07.12
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Length {

  /**
   * 验证失败提示语
   * @return 返回值
   */
  String message() default "";

  int max() default Integer.MAX_VALUE;

  int min() default 0;
}

