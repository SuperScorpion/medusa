package com.jy.medusa.gaze.stuff.cache;

import com.jy.medusa.gaze.stuff.exception.MedusaException;
import com.jy.medusa.gaze.stuff.MySqlGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by neo on 16/10/12.
 * 享元模式
 */
public class MyHelperCacheManager {

    /**
     * 缓存
     * TODO class 要缓存起来的
     */
    private static final Map<String, Class<?>> entityClassMap = new ConcurrentHashMap<>();//缓存class
    private static final Map<String, MySqlGenerator> generatorMap = new ConcurrentHashMap<>();//缓存个 generator


    public static Class<?> getCacheClass(String p) {

        if(p == null) throw new MedusaException("Medusa: The param key is null");

        if(entityClassMap.containsKey(p)) {
            return entityClassMap.get(p);
        } else {
            return null;
        }
    }

    public static void putCacheClass(String p, Class<?> t) {
        if(p != null && t != null) entityClassMap.putIfAbsent(p, t);
    }


    public static MySqlGenerator getCacheGenerator(String p) {

        if(p == null) throw new MedusaException("Medusa: The param key is null");

        if(generatorMap.containsKey(p)) {
            return generatorMap.get(p);
        } else {
            return null;
        }
    }

    public static MySqlGenerator putCacheGenerator(String p, MySqlGenerator t) {
        if(p != null && t != null) {
            return generatorMap.putIfAbsent(p, t);
        } else {
            throw new MedusaException("Medusa: The class info is null");
        }
    }

}