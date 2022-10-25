package com.jy.medusa.validator.annotation;

import java.lang.annotation.*;

/**
 * @author neo 2016.07.12
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Vertifi {
  
  /**
   * 验证失败提示语
   * @return 返回值
   */
  String message() default "";
  
  /**
   * 字段只能选择的值
   * @return 返回值
   */
  String[] selects() default {};


  /**
   * 正则表达式
   * @return 返回值
   */
  String regExp() default "";
}

