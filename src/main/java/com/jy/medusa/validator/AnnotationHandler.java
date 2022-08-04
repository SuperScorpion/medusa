package com.jy.medusa.validator;

import com.jy.medusa.gaze.utils.MedusaCommonUtils;
import com.jy.medusa.validator.annotation.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;


/**
 * 2016.07.11
 * Author neo refactor on 2017.9.1
 * 参数校验主要类
 */
@Aspect
public abstract class AnnotationHandler {

    /**
     *  所有校验标注的处理
     * 	aop:aspectj-autoproxy
     * 	proxy-target-class="true" 设置为cglib代理 但是spring会根据代理类是否有接口 去选择jdk或cglib代理
     *  bean class="com.jy.medusa.validator.AnnotationHandler"
     * @param joinPoint 参数
     * @param parameter 参数
     * @throws IllegalAccessException 异常
     */
    @Before(value = "execution(public * *(..)) && @annotation(parameter)")
    public void paramHandler(JoinPoint joinPoint, ConParamValidator parameter) throws IllegalAccessException {

        List<String> messageList = new ArrayList<>();

        ErrorInfo k = null;

        Class currentClass = joinPoint.getSignature().getDeclaringType();//获取当前class类
        String currentMethodName = joinPoint.getSignature().getName();//获取当前切入方法的名字

        List<Object> paramValueList = Arrays.asList(joinPoint.getArgs());//获取切入方法的入参值

        Method[] medArray = currentClass.getDeclaredMethods();//获取当前类里所有的方法对象

        for(Method method : medArray) {
            int i = 0;
            List<Annotation> methodAnnoList = Arrays.asList(method.getDeclaredAnnotations());//获取当前方法被修饰的所有annotation标签
            if(methodAnnoList.contains(parameter) && currentMethodName.equals(method.getName())) {//1.判断被annotation修饰 //2.判断为当前切入的方法
                Parameter[] params = method.getParameters();//获取当前方法的所有参数对象(声明字段属性等 但是获取不到值)
                for(Parameter p : params) {

                    Length length = p.getDeclaredAnnotation(Length.class);
                    NotNull notNull = p.getDeclaredAnnotation(NotNull.class);
                    Vertifi vertifi = p.getDeclaredAnnotation(Vertifi.class);
                    Valid valid = p.getDeclaredAnnotation(Valid.class);

                    Object paramVal = paramValueList.get(i);

                    if(paramVal != null) {

                        if (length != null) {//处理方法参数是否被标签修饰
                            processLength(length, p.getName(), paramVal, messageList);
                        }
                        if (notNull != null) {//处理方法参数是否被标签修饰
                            processNotNull(notNull, p.getName(), paramVal, messageList);
                        }
                        if (vertifi != null) {//处理方法参数是否被标签修饰
                            processVertifi(vertifi, p.getName(), paramVal, messageList);
                        }
                        if (valid != null) {//处理方法参数是否被标签修饰 并且里面包含各种标签的处理
                            processValid(paramVal, messageList);
                        }

                        if(p.getType() == ErrorInfo.class) {
                            k = ((ErrorInfo) paramVal);
                        }
                    }

                    i+=1;
                }
            }
        }

        //modify by neo on 2017.09.03
//        processErrorMsg(k, messageList);
        //modify by neo on 2022.08.04
        processErrorMsgDefault(k, messageList);
    }

//    public abstract void processErrorMsg(ErrorInfo k, List<String> messageList);

    public void processErrorMsgDefault(ErrorInfo k, List<String> messageList) {
        if(k != null) {
            k.setMessageList(messageList);//等待循环完成后 方法里的参数都校验完 再给结果参数赋值去
        } else if(k == null && !messageList.isEmpty()) {//add by neo on 2022.07.29
             throw new ValidException(messageList.get(0));
        } else {
            //do nothing
        }
    }


        /**
         * 处理length标签
         * @param len 参数
         * @param fieldName 参数
         * @param fieldValue    参数
         * @return 返回值类型
         */
    private void processLength(Length len, String fieldName, Object fieldValue, List<String> messageList) {

        String message = len.message();
        int maxLength = len.max();
        int minLength = len.min();


        if(fieldValue != null) {

            String value = String.valueOf(fieldValue);

            if(value.length() > maxLength || value.length() < minLength) {
                message = MedusaCommonUtils.isNotBlank(message) ? message : "参数" + fieldName + "长度控制在" + minLength + "与" + maxLength + "之间";
                messageList.add(message);
            }
        }
    }

    /**
     * 处理notnull标签
     * @param notNull   参数
     * @param fieldName    参数
     * @param fieldValue       参数
     * @return 返回值类型
     */
    private void processNotNull(NotNull notNull, String fieldName, Object fieldValue, List<String> messageList) {

        String message = notNull.message();

        if(fieldValue == null) {
            message = MedusaCommonUtils.isNotBlank(message) ? message : "参数" + fieldName + "不能为空值";
            messageList.add(message);
        }
    }

    /**
     * 处理vertifi标签
     * @param valid       参数
     * @param fieldName       参数
     * @param fieldValue          参数
     * @return 返回值类型
     */
    private void processVertifi(Vertifi valid, String fieldName, Object fieldValue, List<String> messageList) {

        String message = valid.message();
        String regExp = valid.regExp();
        String[] selects = valid.selects();

        if(fieldValue != null) {

            String value = String.valueOf(fieldValue);//modify by neo 20160128

            if(selects != null && selects.length > 0) {
                List<String> paramList = Arrays.asList(selects);
                if(!paramList.contains(value)) {
                    message = MedusaCommonUtils.isNotBlank(message) ? message : fieldName + "不在设定选定的值内";
                    messageList.add(message);
                }
            } else {
                if(MedusaCommonUtils.isNotBlank(regExp) && !value.matches(regExp)) {
                    message = MedusaCommonUtils.isNotBlank(message) ? message : fieldName + "不符合表达式的校验";
                    messageList.add(message);
                }
            }
        }
    }

    /**
     * 在param 处理valid标签
     * @param obj            参数
     * @return 返回值类型
     * @throws IllegalAccessException
     */
    private void processValid(Object obj, List<String> messageList) throws IllegalAccessException {

        if(obj instanceof Collection<?>) {
            Iterator ir = ((Collection) obj).iterator();
            while(ir.hasNext()) {
                Object childEntity = ir.next();
                entityHandler(childEntity, messageList);
            }
        } else {
            entityHandler(obj, messageList);
        }
    }


    /**
     * 处理实体对象内部的校验标签
     * @param obj        参数
     * @return 返回值类型
     * @throws IllegalAccessException
     */
    private void entityHandler(Object obj, List<String> messageList) throws IllegalAccessException {

        Class cla = obj.getClass();
        Field[] fields = cla.getDeclaredFields();
        Class superClass = cla.getSuperclass();

        if(superClass != null) {
          fields = MedusaCommonUtils.addArrayAll(fields, superClass.getDeclaredFields());
        }

        //遍历对象属性
        for(Field field : fields) {

            field.setAccessible(true);
            Annotation[] annos = field.getAnnotations();

            if(annos != null) {
                for (Annotation anno : annos) {
                    if (NotNull.class.isInstance(anno)) {
                        handlerNotNull(obj, field, anno, messageList);
                    } else if (Vertifi.class.isInstance(anno)) {
                        handleVertifi(obj, field, anno, messageList);
                    } else if (Length.class.isInstance(anno)) {
                        handleLength(obj, field, anno, messageList);
                    } else if (Valid.class.isInstance(anno)) {//实体类的内部的List<Entity>或者Entity
                        Object o = field.get(obj);
                        if(o != null) processValid(o, messageList);
                    }
                }
            }
        }
    }

    /**
     * 处理NotNull
     * */
    private void handlerNotNull(Object source, Field field, Annotation anno, List<String> messageList) throws IllegalAccessException {

        NotNull notNull = (NotNull)anno;

        String fieldName = field.getName();
        Object fieldValue = field.get(source);

        processNotNull(notNull, fieldName, fieldValue, messageList);
    }

  /**
   * 处理 Length标注
   * */
    private void handleLength(Object source, Field field, Annotation anno, List<String> messageList) throws IllegalAccessException {

        Length len = (Length)anno;

        Object fieldValue = field.get(source);
        String fieldName = field.getName();

        processLength(len, fieldName, fieldValue, messageList);
    }


    /**
    * 处理 vertifi标注
    * */
    private void handleVertifi(Object source, Field field, Annotation anno, List<String> messageList) throws IllegalAccessException {

        Vertifi valid = (Vertifi)anno;

        String fieldName = field.getName();
        Object fieldValue = field.get(source);

        processVertifi(valid, fieldName, fieldValue, messageList);
    }
}
