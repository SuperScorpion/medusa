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

    /*private String concatErrorStr(List<ErrorInfo> errorInfos){

        StringBuilder sb ;

        if(!errorInfos.isEmpty()){
            sb = new StringBuilder((6 + 27) * errorInfos.size());
            for(ErrorInfo errorInfo : errorInfos){
                sb.append("**^_^*" + errorInfo.getMessage());
            }

            return sb.toString();
        } else {

            return "";
        }
    }*/


    /**
     * 所有校验标注的处理
     * 	<aop:aspectj-autoproxy />
     * 	proxy-target-class="true" 设置为cglib代理 但是spring会根据代理类是否有接口 去选择jdk或cglib代理
     *  <bean class="com.jy.medusa.validator.AnnotationHandler"/>
     * */
    @Before(value = "execution(public * *(..)) and @annotation(parameter))")
    public void paramHandler(JoinPoint joinPoint, ConParamValidator parameter) throws IllegalAccessException {

        List<String> messageList = new ArrayList<>();

        String[] regArray = parameter.regExp();

        String[] message = parameter.message();

        Class<?> entityClass = parameter.entityClass();

        int i = 0;

        ErrorInfo k = null;

        for(Object object : joinPoint.getArgs()) {

            if (object != null) {

                if (object instanceof Long || object instanceof Integer || object instanceof String
                        || object instanceof Double || object instanceof Float || object instanceof BigDecimal
                        || object instanceof Byte || object instanceof Short) {//TODO 普通参数判断

                    if (i != regArray.length) {

                        if (MyUtils.isNotBlank(object.toString()) && MyUtils.isNotBlank(regArray[i]) && !object.toString().matches(regArray[i])) {

                            if (message != null && message.length > i) {

                                messageList.add(message[i]);
                            } else {

                                messageList.add("第" + (i + 1) + "个普通参数" + object.toString() + "校验失败请重试");
                            }
                        }

                        i++;
                    }

                } else if (object.getClass() == entityClass) {//entity 实体的参数判断
                    messageList.addAll(entityHandler(object));
                } else if (object.getClass() == ErrorInfo.class) {//赋值给方法参数 的回执封装消息
                    k = ((ErrorInfo) object);
                } else {
                    //do nothing
                }
            }
        }

        //modify by neo on 2017.09.03
        if(k != null) k.setMessageList(messageList);//等待循环完成后 方法里的参数都校验完 再给结果参数赋值去
    }

    /**
     * 所有校验标注的处理
     * */
    public List<String> entityHandler(Object obj) throws IllegalArgumentException, IllegalAccessException{

        List<String> errors = new ArrayList<>();

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
    private List<String> handlerNotNull(Object source, Field field, Annotation anno) throws IllegalArgumentException, IllegalAccessException{

        List<String> messageList = new ArrayList<>();

        String fieldName = field.getName();
        Object fieldValue = field.get(source);

        NotNull notNull = (NotNull)anno;
        String message = notNull.message();

        if(fieldValue == null) {
            message = MyUtils.isNotBlank(message) ? message : "参数" + fieldName + "不能为空值";
            messageList.add(message);
        }

        return messageList;
    }
  
    /**
    * 处理Validator标注
    * */
    private List<String> handleValidator(Object source, Field field, Annotation anno) throws IllegalArgumentException, IllegalAccessException{

        List<String> errors = new ArrayList<>();

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
    private List<String> handleLength(Object source, Field field, Annotation anno) throws IllegalArgumentException, IllegalAccessException{

        List<String> messageList = new ArrayList<>();

        Length len = (Length)anno;

        Object fieldValue = field.get(source);
        String fieldName = field.getName();
        String message = len.message();

        int maxLength = len.max();
        int minLength = len.min();


        if(fieldValue != null){

            String value = String.valueOf(fieldValue);

            if(value.length() > maxLength || value.length() < minLength){
                message = MyUtils.isNotBlank(message) ? message : "参数" + fieldName + "长度控制在" + minLength + "与" + maxLength + "之间";
                messageList.add(message);
            }
        }

        return messageList;
    }

    private List<String> handleParams(Object fieldValue, String fieldName, Validator valid){

        List<String> messageList = new ArrayList<>();

        String message = valid.message();
        String regExp = valid.regExp();

        String[] selects = valid.selects();

            if(fieldValue != null) {

            String value = String.valueOf(fieldValue);
              //modify by neo 20160128
                if(selects != null && selects.length > 0) {
                    List<String> paramList = Arrays.asList(selects);
                    if(!paramList.contains(value)){
                        message = MyUtils.isNotBlank(message) ? message : fieldName + "不在设定选定的值内";
                        messageList.add(message);
                    }
                } else {
                    if(MyUtils.isNotBlank(regExp) && !value.matches(regExp)) {
                        message = MyUtils.isNotBlank(message) ? message : fieldName + "不符合表达式的校验";
                        messageList.add(message);
                    }
                }
            }

        return messageList;
    }
}
