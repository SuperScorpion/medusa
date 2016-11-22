package com.jy.medusa.validator.annotation;

import java.lang.annotation.*;

/**
 * add by neo 2016.07.12
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validator {
  
  /** 验证失败提示语 */
  public String message() default ""; 
  
  /** 字段只能选择的值  */
  public String[] selects() default {}; 
  
  /** 正则表达式 **/
  public String regExp() default "";
  
}

