package com.jy.medusa.validator;


import com.jy.medusa.utils.SystemConfigs;
import com.jy.medusa.validator.annotation.ConParamValidator;
import com.jy.medusa.validator.annotation.Length;
import com.jy.medusa.validator.annotation.Validator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;

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
public class AnnotationHandler {
	
 	/*public void takeSeats(JoinPoint joinPoint, Validator parameter) throws NoSuchMethodException, SecurityException{

 		System.out.println(joinPoint.getKind());
 		System.out.println(joinPoint.getTarget());
 		System.out.println(joinPoint.getThis());
 		System.out.println(joinPoint.getArgs());
 		System.out.println(joinPoint.getSignature().getName());
    }*/
    public String concatErrorStr(List<ErrorInfo> errorInfos){

        StringBuilder sb = new StringBuilder(100);

        if(!errorInfos.isEmpty()){
            for(ErrorInfo errorInfo : errorInfos){
                sb.append("**^_^*" + errorInfo.getMessage());
            }
        }

        return sb.toString();
    }

    private final String validatorPath = SystemConfigs.VALID_VALIDATOR_PATH;
    private final String lengthPath = SystemConfigs.VALID_LENGTH_PATH;


    /**
     * 所有校验标注的处理
     * */
    public void paramHandler(JoinPoint joinPoint, ConParamValidator parameter) throws IllegalArgumentException, IllegalAccessException{

//        if(!errorInfoList.isEmpty()) errorInfoList.clear();

        List<ErrorInfo> errorInfoList = new ArrayList<>();

        String[] regArray = parameter.regExp();

        String message = parameter.message();

        Class<?> entityClass = parameter.entityClass();

        int i = 0;

        ErrorInfo k = null;

        for(Object object : joinPoint.getArgs()){

            if(object == null) continue;

            if(object instanceof Long || object instanceof Integer || object instanceof String
                    || object instanceof Double || object instanceof Float || object instanceof BigDecimal) {//TODO 普通参数判断

                /*for(String regExp : regArray){
                    if(StringUtils.isNotBlank(object.toString()) && StringUtils.isNotBlank(regExp) && object.toString().matches(regExp)) {
                        var1 = Boolean.TRUE;
                        break;
                    }
                }*/
                if(StringUtils.isNotBlank(object.toString()) && StringUtils.isNotBlank(regArray[i]) && !object.toString().matches(regArray[i])) {

                    ErrorInfo errorInfo = new ErrorInfo();
                    errorInfo.setMessage("第" + (i+1) + "个参数" + object.toString() + "校验失败请重试");
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
      fields = ArrayUtils.addAll(fields, superClass.getDeclaredFields());
    }

    //遍历对象属性
    for(Field field : fields) {
      field.setAccessible(true);
      Annotation[] annos = field.getAnnotations();
      for(Annotation anno : annos) {
        if(anno.annotationType().getName().equals(validatorPath)) {

          errors.addAll(handleValidator(obj, field, anno));

        } else if(anno.annotationType().getName().equals(lengthPath)) {

          errors.addAll(handleLength(obj, field, anno));

        }
      }
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

    Object fieldValue = field.get(source);
    String fieldName = field.getName();

    Length len = (Length) anno;

    int maxLength = len.max();
    int minLength = len.min();

    if(fieldValue != null){

      String value = String.valueOf(fieldValue);

      if(value.length() > maxLength || value.length() < minLength){

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setMessage("参数" + fieldName + "长度控制在" + minLength + "与" + maxLength + "之间");
        errors.add(errorInfo);
      }
    }
    return errors;
  }
  
  private List<ErrorInfo> handleParams(Object fieldValue, String fieldName, Validator valid){

    List<ErrorInfo> errors = new ArrayList<>();

    String message = StringUtils.isNotBlank(valid.message()) ? valid.message() : fieldName + "不符合校验表达式";
    String regExp = valid.regExp();

    String[] selects = valid.selects();
    
    if(fieldValue == null) {
	      ErrorInfo errorInfo = new ErrorInfo();
	      errorInfo.setMessage(fieldName + "必填");
	      errors.add(errorInfo);
    } else {
	      String value = String.valueOf(fieldValue);
	      
	      //modify by neo 20160128
	      if(selects != null && selects.length > 0) {
	    	  List<String> paramList = Arrays.asList(selects);
	    	  if(!paramList.contains(value)){
	    		  ErrorInfo errorInfo = new ErrorInfo();
	    		  errorInfo.setMessage(fieldName + "不在设定选定值内");
	    		  errors.add(errorInfo);
	    	  }
	      } else {
	    	  if(StringUtils.isNotBlank(regExp) && !value.matches(regExp)) {
	    		  ErrorInfo errorInfo = new ErrorInfo();
	    		  errorInfo.setMessage(message);
	    		  errors.add(errorInfo);
	    	  }
	      }
    }

    return errors;
  }
}
