package com.jy.medusa.validator.annotation;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {

  /** 验证失败提示语 */
  public String message() default "";
}

