package com.jy.medusa.validator.annotation;

import java.lang.annotation.*;

/**
 * @Author neo 2016.07.12
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Vertifi {
  
  /** 验证失败提示语 */
  String message() default "";
  
  /** 字段只能选择的值  */
  String[] selects() default {};
  
  /** 正则表达式 **/
  String regExp() default "";
}

