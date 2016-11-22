package com.jy.medusa.validator.annotation;

import java.lang.annotation.*;

/**
 * add by neo 20170713
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConParamValidator {

  /** 验证失败提示语 */
  public String message() default "";

  /** 正则表达式 **/
  public String[] regExp() default "";

  public Class<? extends Object> entityClass() default Object.class;
}
