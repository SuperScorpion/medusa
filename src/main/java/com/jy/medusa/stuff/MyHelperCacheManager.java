package com.jy.medusa.stuff;

import com.jy.medusa.utils.MySqlGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neo on 16/10/12.
 * 享元模式
 */
public class MyHelperCacheManager {

    /**
     * 缓存
     * TODO class 要缓存起来的
     */
    private static final Map<String, Class<?>> entityClassMap = new HashMap<>();//缓存class
    private static final Map<String, MySqlGenerator> generatorMap = new HashMap<>();//缓存个 generator


    public static Class<?> getCacheClass(String p) {

        if(p == null) return null;

        if(entityClassMap.containsKey(p)) {
            return entityClassMap.get(p);
        }

        return null;
    }

    public static void putCacheClass(String p, Class<?> t) {
        if(p != null && t != null) entityClassMap.put(p, t);
    }


    public static MySqlGenerator getCacheGenerator(String p) {

        if(p == null) return null;

        if(generatorMap.containsKey(p)) {
            return generatorMap.get(p);
        }

        return null;
    }

    public static void putCacheGenerator(String p, MySqlGenerator t) {
        if(p != null && t != null) generatorMap.put(p, t);
    }

}
