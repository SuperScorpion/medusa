package com.jy.medusa.validator;
/**
 * @desc 参数校验错误信息封装类
 */
public class ErrorInfo implements java.io.Serializable{
  
  /** 错误信息 **/
  private String message;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}

