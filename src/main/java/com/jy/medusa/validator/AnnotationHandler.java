package com.jy.medusa.validator;


import com.jy.medusa.utils.MyUtils;
import com.jy.medusa.validator.annotation.ConParamValidator;
import com.jy.medusa.validator.annotation.Length;
import com.jy.medusa.validator.annotation.NotNull;
import com.jy.medusa.validator.annotation.Validator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 2016.07.11
 * 参数校验主要类
 */
@Aspect
public class AnnotationHandler {//TODO
	
    private String concatErrorStr(List<ErrorInfo> errorInfos){

        StringBuilder sb = new StringBuilder(100);

        if(!errorInfos.isEmpty()){
            for(ErrorInfo errorInfo : errorInfos){
                sb.append("**^_^*" + errorInfo.getMessage());
            }
        }

        return sb.toString();
    }

/*    private final String validatorPath = SystemConfigs.VALID_VALIDATOR_PATH;

    private final String lengthPath = SystemConfigs.VALID_LENGTH_PATH;

    private final String notNullPath = SystemConfigs.VALID_NOTNULL_PATH;*/

    /**
     * 所有校验标注的处理
     * 	<aop:aspectj-autoproxy proxy-target-class="true"/>
     *  <bean class="com.jy.medusa.validator.AnnotationHandler"/>
     * */
//    @Pointcut(value= "execution(* *.*ServiceImpl.*(..)) and @annotation(parameter))")
    @Before(value = "execution(public * *(..)) and @annotation(parameter))")
    public void paramHandler(JoinPoint joinPoint, ConParamValidator parameter) throws IllegalArgumentException, IllegalAccessException{

//        if(!errorInfoList.isEmpty()) errorInfoList.clear();

        List<ErrorInfo> errorInfoList = new ArrayList<>();

        String[] regArray = parameter.regExp();

        String[] message = parameter.message();

        Class<?> entityClass = parameter.entityClass();

        int i = 0;

        ErrorInfo k = null;

        for(Object object : joinPoint.getArgs()){

            if(object == null) continue;

            if(object instanceof Long || object instanceof Integer || object instanceof String
                    || object instanceof Double || object instanceof Float || object instanceof BigDecimal) {//TODO 普通参数判断

                if(i == regArray.length) continue;

                if(MyUtils.isNotBlank(object.toString()) && MyUtils.isNotBlank(regArray[i]) && !object.toString().matches(regArray[i])) {

                    ErrorInfo errorInfo = new ErrorInfo();

                    if(message != null && message.length > i) {

                        errorInfo.setMessage(message[i]);
                    } else {

                        errorInfo.setMessage("第" + (i+1) + "个普通参数" + object.toString() + "校验失败请重试");
                    }

                    errorInfoList.add(errorInfo);
                }

                i++;

            } else if(object.getClass() == entityClass) {//entity 实体的参数判断
                errorInfoList.addAll(entityHandler(object));
            } else if(object.getClass() == ErrorInfo.class){//赋值给方法参数 的回执封装消息
                k = ((ErrorInfo) object);
            }
        }

        if(k != null) k.setMessage(concatErrorStr(errorInfoList));//等待循环完成后 方法里的参数都校验完 再给结果参数赋值去
    }

    /**
     * 所有校验标注的处理
     * */
  public List<ErrorInfo> entityHandler(Object obj) throws IllegalArgumentException, IllegalAccessException{

      //if(!errorInfoList.isEmpty()) clearErrorInfoList();

    List<ErrorInfo> errors = new ArrayList<>();

//    Object obj = joinPoint.getThis();
    Class cla = obj.getClass();
    Field[] fields = cla.getDeclaredFields();
    Class superClass = cla.getSuperclass();

    if(superClass != null) {
      fields = MyUtils.addArrayAll(fields, superClass.getDeclaredFields());
    }

    //遍历对象属性
    for(Field field : fields) {
      field.setAccessible(true);
      Annotation[] annos = field.getAnnotations();
      for(Annotation anno : annos) {
          if(NotNull.class.isInstance(anno)) {

            errors.addAll(handlerNotNull(obj, field, anno));

          } else if(Validator.class.isInstance(anno)) {

            errors.addAll(handleValidator(obj, field, anno));

          } else if(Length.class.isInstance(anno)) {

            errors.addAll(handleLength(obj, field, anno));
        }
      }
    }

    return errors;
  }

    /**
     * 处理NotNull
     * */
    private List<ErrorInfo> handlerNotNull(Object source, Field field, Annotation anno) throws IllegalArgumentException, IllegalAccessException{

        List<ErrorInfo> errors = new ArrayList<>();

        String fieldName = field.getName();
        Object fieldValue = field.get(source);

        NotNull notNull = (NotNull)anno;
        String message = notNull.message();

        if(fieldValue == null) {
            ErrorInfo errorInfo = new ErrorInfo();
            message = MyUtils.isNotBlank(message) ? message : "参数" + fieldName + "不能为空值";
            errorInfo.setMessage(message);
            errors.add(errorInfo);
        }

        return errors;
    }
  
  /**
   * 处理Validator标注
   * */
  private List<ErrorInfo> handleValidator(Object source, Field field, Annotation anno) throws IllegalArgumentException, IllegalAccessException{
	  
    List<ErrorInfo> errors = new ArrayList<>();
    
    String fieldName = field.getName();
    Object fieldValue = field.get(source);
    
    Validator valid = (Validator)anno;
    //String message = validator.message();
    //String regExp = validator.regExp();

    errors.addAll(handleParams(fieldValue, fieldName, valid));

    return errors;
  }
  
  /**
   * 处理 Length标注
   * */
  private List<ErrorInfo> handleLength(Object source, Field field, Annotation anno) throws IllegalArgumentException, IllegalAccessException{

    List<ErrorInfo> errors = new ArrayList<>();

    Length len = (Length)anno;

    Object fieldValue = field.get(source);
    String fieldName = field.getName();
    String message = len.message();

    int maxLength = len.max();
    int minLength = len.min();


    if(fieldValue != null){

      String value = String.valueOf(fieldValue);

      if(value.length() > maxLength || value.length() < minLength){

        ErrorInfo errorInfo = new ErrorInfo();
        message = MyUtils.isNotBlank(message) ? message : "参数" + fieldName + "长度控制在" + minLength + "与" + maxLength + "之间";
        errorInfo.setMessage(message);
        errors.add(errorInfo);
      }
    }
    return errors;
  }
  
  private List<ErrorInfo> handleParams(Object fieldValue, String fieldName, Validator valid){

    List<ErrorInfo> errors = new ArrayList<>();

    String message = valid.message();
    String regExp = valid.regExp();

    String[] selects = valid.selects();

    /*if(fieldValue == null) {
	      ErrorInfo errorInfo = new ErrorInfo();
	      errorInfo.setMessage(fieldName + "不能为空值");
	      errors.add(errorInfo);
    } else {*/

      if(fieldValue != null) {

        String value = String.valueOf(fieldValue);
	      //modify by neo 20160128
          if(selects != null && selects.length > 0) {
              List<String> paramList = Arrays.asList(selects);
              if(!paramList.contains(value)){
                  ErrorInfo errorInfo = new ErrorInfo();
                  message = MyUtils.isNotBlank(message) ? message : fieldName + "不在设定选定的值内";
                  errorInfo.setMessage(message);
                  errors.add(errorInfo);
              }
          } else {
              if(MyUtils.isNotBlank(regExp) && !value.matches(regExp)) {
                  ErrorInfo errorInfo = new ErrorInfo();
                  message = MyUtils.isNotBlank(message) ? message : fieldName + "不符合表达式的校验";
                  errorInfo.setMessage(message);
                  errors.add(errorInfo);
              }
          }
    }

    return errors;
  }
}
