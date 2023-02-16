package com.jy.medusa.gaze.utils;

import com.jy.medusa.generator.MedusaGenUtils;
import com.jy.medusa.gaze.stuff.cache.MyReflectCacheManager;
import com.jy.medusa.gaze.stuff.exception.MedusaException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by SuperScorpion on 16/7/27.
 */
public class MedusaReflectionUtils {

    /**
     * 调用Getter方法
     * @param obj 参数
     * @param propertyName 字段名
     * @return 返回值类型
     */
    public static Object invokeGetterMethod(Object obj, String propertyName) {
        String getterMethodName = "get" + MedusaGenUtils.upcaseFirst(propertyName);
        return invokeMethod(obj, getterMethodName, null, null);
    }

    /**
     * 调用Setter方法,不指定参数的类型
     * @param obj 参数
     * @param propertyName 字段名
     * @param value 参数
     * @throws ParseException 异常
     */
    public static void invokeSetterMethod(Object obj, String propertyName, Object value) throws ParseException {
        invokeSetterMethod(obj, propertyName, value, null);
    }

    /**
     * 调用Setter方法,指定参数的类型
     * @param obj 参数
     * @param propertyName 字段名
     * @param value 参数
     * @param propertyType 参数
     * @throws ParseException 异常
     */
    public static void invokeSetterMethod(Object obj, String propertyName, Object value, Class<?> propertyType) throws ParseException {

        value = handleValueType(obj, propertyName, value);
        if (value == null) return;

        propertyType = propertyType != null ? propertyType : value.getClass();
        String setterMethodName = "set" + MedusaGenUtils.upcaseFirst(propertyName);
        invokeMethod(obj, setterMethodName, new Class<?>[]{propertyType}, new Object[]{value});
    }

    private static Object handleValueType(Object obj, String propertyName, Object value) throws ParseException {

        String getterMethodName = "get" + MedusaGenUtils.upcaseFirst(propertyName);
        Class<?> argsType = value.getClass();
        Class<?> returnType = obtainAccessibleMethod(obj, getterMethodName).getReturnType();

        if (argsType == returnType) {
            return value;
        }

        if (returnType == Boolean.class) {
            String temp = value.toString();
            value = (MedusaCommonUtils.isNotBlank(temp) && Long.valueOf(temp) > 0) ? true : false;
        } else if (returnType == Long.class) {
            value = Long.valueOf(value.toString());
        } else if (returnType == Date.class) {
            value = MedusaDateUtils.convertStrToDate(value.toString());//TODO
        } else if (returnType == Short.class) {
            value = Short.valueOf(value.toString());
        } else if (returnType == BigDecimal.class) {
            value = BigDecimal.valueOf(Long.valueOf(value.toString()));
        } else if (returnType == BigInteger.class) {
            value = BigInteger.valueOf(Long.valueOf(value.toString()));
        } else if (returnType == String.class) {
            value = String.valueOf(value);
        } else if (returnType == Integer.class) {
            value = Integer.valueOf(value.toString());
        } else if (returnType == Byte.class) {
            value = Byte.valueOf(value.toString());
        } else {
//            do nothing
        }

        return value;
    }

    /**
     * 直接调用对象方法，忽视private/protected修饰符
     * @param obj 参数
     * @param methodName 参数
     * @param parameterTypes 参数
     * @param args 参数
     * @return 返回值类型
     */
    public static Object invokeMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object[] args) {

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
     * @param obj 参数
     * @param methodName 参数
     * @param parameterTypes 参数
     * @return 返回值类型
     */
    public static Method obtainAccessibleMethod(Object obj, String methodName, Class<?>... parameterTypes) {

        Class<?> realClass = obj.getClass();
        Class<Object> objClass = Object.class;

        for (; realClass != objClass; realClass = realClass.getSuperclass()) {

            if (realClass != null) {

                Method[] methods = MyReflectCacheManager.getCacheMethodArray(realClass);

                if (methods != null && methods.length != 0) {
                    for (Method med : methods) {
                        if (med != null && med.getName().equals(methodName)) {
                            med.setAccessible(true);
                            return med;
                        }
                    }
                }
            }
        }

        throw new MedusaException("Medusa: Cant find [" + methodName + "] on " + realClass.getName());
    }

    /**
     * 直接读取对象属性值 忽视private/protected修饰符，不经过getter函数
     * @param obj 参数
     * @param fieldName 参数
     * @return 返回值类型
     */
    public static Object obtainFieldValue(Object obj, String fieldName) {

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
     * @param obj 参数
     * @param fieldName 参数
     * @param value 参数
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {

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
     * @param obj 参数
     * @param fieldName 参数
     * @return 返回值类型
     */
    public static Field obtainAccessibleField(Object obj, String fieldName) {

        Class<?> realClass = obj.getClass();
        Class<Object> objClass = Object.class;

        for (; realClass != objClass; realClass = realClass.getSuperclass()) {

            if (realClass != null) {

                Field[] cacheFields = MyReflectCacheManager.getCacheFieldArray(realClass);////从缓存读取

                if (cacheFields != null && cacheFields.length != 0) {
                    for (Field field : cacheFields) {
                        if (field != null && field.getName().equals(fieldName)) {
                            field.setAccessible(true);
                            return field;
                        }
                    }
                }
            }
        }

        throw new MedusaException("Medusa: Cant find [" + fieldName + "] on " + realClass.getName());
    }
}
