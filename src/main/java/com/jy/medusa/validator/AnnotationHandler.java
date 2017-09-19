package com.jy.medusa.validator;

import com.jy.medusa.utils.MyUtils;
import com.jy.medusa.validator.annotation.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 2016.07.11
 * @Author neo refactor on 2017.9.1
 * 参数校验主要类
 */
@Aspect
public class AnnotationHandler {

    /**
     * 所有校验标注的处理
     * 	<aop:aspectj-autoproxy />
     * 	proxy-target-class="true" 设置为cglib代理 但是spring会根据代理类是否有接口 去选择jdk或cglib代理
     *  <bean class="com.jy.medusa.validator.AnnotationHandler"/>
     * */
    @Before(value = "execution(public * *(..)) and @annotation(parameter))")
    public void paramHandler(JoinPoint joinPoint, ConParamValidator parameter) throws IllegalAccessException {

        List<String> messageList = new ArrayList<>();

        int i = 0;

        ErrorInfo k = null;

        Signature sig = joinPoint.getSignature();
        Class sigCls = sig.getDeclaringType();

        List<Object> paramValueList = Arrays.asList(joinPoint.getArgs());

        Method[] medArray = sigCls.getDeclaredMethods();

        for(Method method : medArray) {
            List<Annotation> methodList = Arrays.asList(method.getDeclaredAnnotations());
            if(methodList.contains(parameter)) {
                Parameter[] params = method.getParameters();
                for(Parameter p : params) {

                    Length length = p.getDeclaredAnnotation(Length.class);
                    NotNull notNull = p.getDeclaredAnnotation(NotNull.class);
                    Vertifi vertifi = p.getDeclaredAnnotation(Vertifi.class);
                    Valid valid = p.getDeclaredAnnotation(Valid.class);

                    Object paramVal = paramValueList.get(i);

                    if(length != null) {messageList.addAll(processLength(length, p.getName(), paramVal));}
                    if(notNull != null) {messageList.addAll(processNotNull(notNull, p.getName(), paramVal));}
                    if(vertifi != null) {messageList.addAll(processVertifi(vertifi, p.getName(), paramVal));}
                    if(valid != null) {messageList.addAll(processValid(paramVal));}

                    if(p.getType() == ErrorInfo.class) {
                        k = ((ErrorInfo) paramVal);
                    }

                    i=i+1;
                }
            }
        }

        //modify by neo on 2017.09.03
        if(k != null) k.setMessageList(messageList);//等待循环完成后 方法里的参数都校验完 再给结果参数赋值去
    }


    /**
     * 处理length标签
     * @param len
     * @param fieldName
     * @param fieldValue
     * @return
     */
    private List<String> processLength(Length len, String fieldName, Object fieldValue) {

        List<String> messageList = new ArrayList<>();

        String message = len.message();
        int maxLength = len.max();
        int minLength = len.min();


        if(fieldValue != null) {

            String value = String.valueOf(fieldValue);

            if(value.length() > maxLength || value.length() < minLength) {
                message = MyUtils.isNotBlank(message) ? message : "参数" + fieldName + "长度控制在" + minLength + "与" + maxLength + "之间";
                messageList.add(message);
            }
        }

        return messageList;
    }

    /**
     * 处理notnull标签
     * @param notNull
     * @param fieldName
     * @param fieldValue
     * @return
     */
    private List<String> processNotNull(NotNull notNull, String fieldName, Object fieldValue) {

        List<String> messageList = new ArrayList<>();

        String message = notNull.message();

        if(fieldValue == null) {
            message = MyUtils.isNotBlank(message) ? message : "参数" + fieldName + "不能为空值";
            messageList.add(message);
        }

        return messageList;
    }

    /**
     * 处理vertifi标签
     * @param valid
     * @param fieldName
     * @param fieldValue
     * @return
     */
    private List<String> processVertifi(Vertifi valid, String fieldName, Object fieldValue) {

        List<String> messageList = new ArrayList<>();

        String message = valid.message();
        String regExp = valid.regExp();
        String[] selects = valid.selects();

        if(fieldValue != null) {

            String value = String.valueOf(fieldValue);//modify by neo 20160128

            if(selects != null && selects.length > 0) {
                List<String> paramList = Arrays.asList(selects);
                if(!paramList.contains(value)) {
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

    /**
     * 在param 处理valid标签
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    private List<String> processValid(Object obj) throws IllegalAccessException {

        return entityHandler(obj);
    }


    /**
     * 处理实体对象内部的校验标签
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    private List<String> entityHandler(Object obj) throws IllegalAccessException {

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

            if(annos != null) {
                for (Annotation anno : annos) {
                    if (NotNull.class.isInstance(anno)) {
                        errors.addAll(handlerNotNull(obj, field, anno));
                    } else if (Vertifi.class.isInstance(anno)) {
                        errors.addAll(handleVertifi(obj, field, anno));
                    } else if (Length.class.isInstance(anno)) {
                        errors.addAll(handleLength(obj, field, anno));
                    }
                }
            }
        }

        return errors;
    }

    /**
     * 处理NotNull
     * */
    private List<String> handlerNotNull(Object source, Field field, Annotation anno) throws IllegalAccessException {

        NotNull notNull = (NotNull)anno;

        String fieldName = field.getName();
        Object fieldValue = field.get(source);

        return processNotNull(notNull, fieldName, fieldValue);
    }

  /**
   * 处理 Length标注
   * */
    private List<String> handleLength(Object source, Field field, Annotation anno) throws IllegalAccessException {

        Length len = (Length)anno;

        Object fieldValue = field.get(source);
        String fieldName = field.getName();

        return processLength(len, fieldName, fieldValue);
    }


    /**
    * 处理 vertifi标注
    * */
    private List<String> handleVertifi(Object source, Field field, Annotation anno) throws IllegalAccessException {

        Vertifi valid = (Vertifi)anno;

        String fieldName = field.getName();
        Object fieldValue = field.get(source);

        return processVertifi(valid, fieldName, fieldValue);
    }
}
