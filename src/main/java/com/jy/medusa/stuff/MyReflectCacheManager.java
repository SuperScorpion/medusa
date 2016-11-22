package com.jy.medusa.stuff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by neo on 16/10/12.
 * 享元模式
 */
public class MyReflectCacheManager {

    private static final Logger logger = LoggerFactory.getLogger(MyReflectCacheManager.class);

    /**
     * 缓存方法
     */
    private static final Map<Class<?>, Method[]> METHODS_CACHEMAP = new HashMap<>();///缓存methods
    private static final Map<Class<?>, Field[]> FIELDS_CACHEMAP = new HashMap<>();/////缓存fields


    /**
     * 反射调用setter方法时会触发
     * @param c
     * @return
     */
    public static Method[] getCacheMethodArray(Class<?> c) {

        if(c == null) return null;

        if(METHODS_CACHEMAP.containsKey(c)) {
            return METHODS_CACHEMAP.get(c);
        }

        Method[] paramMethods = c.getDeclaredMethods();
        METHODS_CACHEMAP.put(c, paramMethods);

        logger.debug("Neo: " + c.getName() + " Methods 全部缓存初始化");

        return paramMethods;
    }

    public static Field[] getCacheFieldArray(Class<?> c) {

        if(c == null) return null;

        if(FIELDS_CACHEMAP.containsKey(c)) {
            return FIELDS_CACHEMAP.get(c);
        }

        Field[] paramFields = c.getDeclaredFields();
        FIELDS_CACHEMAP.put(c, paramFields);

        logger.debug("Neo: " + c.getName() + " Fields 全部缓存初始化");

        return paramFields;
    }

}
