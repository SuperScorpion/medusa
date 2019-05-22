package com.jy.medusa.validator;

import java.util.List;

/**
 * 参数校验错误信息封装类
 */
public class ErrorInfo implements java.io.Serializable{
  
  /** 错误信息 **/
  private List<String> messageList;

  public List<String> getMessageList() {
    return messageList;
  }

  public void setMessageList(List<String> messageList) {
    this.messageList = messageList;
  }
}

