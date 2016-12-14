package com.jy.medusa.utils;

import com.jy.medusa.generator.MyGenUtils;
import com.jy.medusa.stuff.cache.MyReflectCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by neo on 16/7/27.
 */
public class MyReflectionUtils {

    private static final Logger logger = LoggerFactory.getLogger(MyReflectionUtils.class);

    /**
     * 缓存方法
     */
   // private static final Map<Class<?>, Method[]> METHODS_CACHEMAP = new HashMap<>();///缓存methods
  ///  private static final Map<Class<?>, Field[]> FIELDS_CACHEMAP = new HashMap<>();/////缓存fields

    /**
     * 调用Getter方法
     * @param obj
     * @param propertyName
     * @return
     */
    public static Object invokeGetterMethod(Object obj, String propertyName) {
        String getterMethodName = "get" + MyGenUtils.upcaseFirst(propertyName);
        return invokeMethod(obj, getterMethodName, null, null);
    }

    /**
     * 调用Setter方法,不指定参数的类型
     * @param obj
     * @param propertyName
     * @param value
     */
    public static void invokeSetterMethod(Object obj, String propertyName, Object value) throws ParseException {
        invokeSetterMethod(obj, propertyName, value, null);
    }

    /**
     * 调用Setter方法,指定参数的类型
     * @param obj
     * @param propertyName  字段名
     * @param value
     * @param propertyType
     */
    public static void invokeSetterMethod(Object obj, String propertyName, Object value, Class<?> propertyType) throws ParseException {

        value = handleValueType(obj,propertyName,value);
        if(value == null) return;

        propertyType = propertyType != null ? propertyType : value.getClass();
        String setterMethodName = "set" + MyGenUtils.upcaseFirst(propertyName);
        invokeMethod(obj, setterMethodName, new Class<?>[] { propertyType }, new Object[] { value });
    }

    private static Object handleValueType(Object obj, String propertyName, Object value) throws ParseException {

        String getterMethodName = "get" + MyGenUtils.upcaseFirst(propertyName);
        Class<?> argsType = value.getClass();
        Class<?> returnType = obtainAccessibleMethod(obj, getterMethodName).getReturnType();

        if(argsType == returnType) {
            return value;
        }

        if (returnType == Boolean.class) {
            String temp = value.toString();
            value = (MyUtils.isNotBlank(temp) && Long.valueOf(temp) > 0) ? true : false;
        } else if (returnType == Long.class) {
            value = Long.valueOf(value.toString());
        }else if(returnType == Date.class){
            value = MyDateUtils.convertStrToDate(value.toString());//TODO
        } else if (returnType == Short.class) {
            value = Short.valueOf(value.toString());
        } else if (returnType == BigDecimal.class) {
            value = BigDecimal.valueOf(Long.valueOf(value.toString()));
        } else if (returnType == BigInteger.class) {
            value = BigInteger.valueOf(Long.valueOf(value.toString()));
        } else if(returnType == String.class){
            value = String.valueOf(value);
        }else if(returnType == Integer.class){
            value = Integer.valueOf(value.toString());
        } else if(returnType == Byte.class){
            value = Byte.valueOf(value.toString());
        }

        return value;
    }

    /**
     * 直接调用对象方法，忽视private/protected修饰符
     * @param obj
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes, final Object[] args) {

        Method method = obtainAccessibleMethod(obj, methodName, parameterTypes);

        if (method == null) throw new IllegalArgumentException("Medusa: Could not find method [" + methodName + "] on target [" + obj + "].");

        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 循环向上转型，获取对象的DeclaredMethod,并强制设置为可访问 如向上转型到Object仍无法找到，返回null
     * 用于方法需要被多次调用的情况，先使用本函数先取得Method,然后调用Method.invoke(Object obj,Object...args)
     * @param obj
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method obtainAccessibleMethod(final Object obj, final String methodName, final Class<?>... parameterTypes) {

        Class<?> superClass = obj.getClass();
        Class<Object> objClass = Object.class;

        Method method = null;

        try {
            for (; superClass != objClass; superClass = superClass.getSuperclass()) {

                if(superClass == null) continue;

                    Method[] methods = MyReflectCacheManager.getCacheMethodArray(superClass);

                    if(methods != null && methods.length != 0) {
                        for(Method med : methods) {
                            if(med.getName().equals(methodName)) {
                                method = med;
                                break;
                            }
                        }
                    } //else {
                        //METHODS_CACHEMAP.put(superClass, superClass.getDeclaredMethods());
                      //  method = superClass.getDeclaredMethod(methodName, parameterTypes);
                    //}

                    if(method != null) {
                        method.setAccessible(true);
                        return method;
                    }
            }
        } catch (Exception e) {
            logger.error("Medusa: There was an exception in the reflection " + superClass.getName() + " get the Method " + methodName);
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 直接读取对象属性值 忽视private/protected修饰符，不经过getter函数
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object obtainFieldValue(final Object obj, final String fieldName) {

        Field field = obtainAccessibleField(obj, fieldName);

        if (field == null) throw new IllegalArgumentException("Medusa: could not find field [" + fieldName + "] on target [" + obj + "]");

        Object retval = null;

        try {
            retval = field.get(obj);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return retval;
    }

    /**
     * 直接设置对象属性值 忽视private/protected修饰符，不经过setter函数
     * @param obj
     * @param fieldName
     * @param value
     */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value) {

        Field field = obtainAccessibleField(obj, fieldName);

        if (field == null) throw new IllegalArgumentException("Medusa: could not find field [" + fieldName + "] on target [" + obj + "]");

        try {
            field.set(obj, value);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 循环向上转型，获取对象的DeclaredField,并强制设为可访问 如向上转型Object仍无法找到，返回null
     * @param obj
     * @param fieldName
     * @return
     */
    public static Field obtainAccessibleField(final Object obj, final String fieldName) {

        Class<?> superClass = obj.getClass();
        Class<Object> objClass = Object.class;

        Field field = null;

        try {
            for (; superClass != objClass; superClass = superClass.getSuperclass()) {

                if(superClass == null) continue;

                    Field[] cacheFields = MyReflectCacheManager.getCacheFieldArray(superClass);////从缓存读取

                    if(cacheFields != null && cacheFields.length != 0) {
                        for(Field field2 : cacheFields) {
                            if(field2.getName().equals(fieldName)) {
                                field = field2;
                                break;
                            }
                        }
                    }
                    //} else {
                    //    FIELDS_CACHEMAP.put(superClass, superClass.getDeclaredFields());///加入到类的缓存 参数域
                    //    field = superClass.getDeclaredField(fieldName);
                    //}

                    if(field != null) {
                        field.setAccessible(true);
                        return field;
                    }
            }
        } catch (Exception e) {
            logger.error("Medusa: There was an exception in the reflection " + superClass.getName() + " get the Field " + fieldName);
            e.printStackTrace();
        }

        return null;
    }
}
