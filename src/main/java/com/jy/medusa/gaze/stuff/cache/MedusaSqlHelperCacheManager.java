package com.jy.medusa.gaze.stuff.cache;

import com.jy.medusa.gaze.stuff.MedusaSqlGenerator;
import com.jy.medusa.gaze.stuff.exception.MedusaException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SuperScorpion on 16/10/12.
 * 享元模式
 * @author SuperScorpion
 */
public class MedusaSqlHelperCacheManager {

    /**
     * Cache Class and MedusaSqlGenerator
     */
    private static final Map<String, Class<?>> entityClassMap = new ConcurrentHashMap<>();//缓存class @unused
    private static final Map<String, MedusaSqlGenerator> generatorCacheMap = new ConcurrentHashMap<>();//缓存generator (key:xxx.xxx.xxxMapper)
    private static final Map<Class<?>, MedusaSqlGenerator> generatorSouCacheMap = new ConcurrentHashMap<>();//缓存generator (key:实体类的class类 for saveOrUpdate)


    public static Class<?> getCacheClass(String p) {

        if(p == null) throw new MedusaException("Medusa: The param key is null");

        if(entityClassMap.containsKey(p)) {
            return entityClassMap.get(p);
        } else {
            return null;
        }
    }

    public static void putCacheClass(String p, Class<?> t) {
        if(p != null && t != null) {
            entityClassMap.putIfAbsent(p, t);
        } else {
            throw new MedusaException("Medusa: The param key or value is null");
        }
    }


    public static MedusaSqlGenerator getCacheGeneratorByMapperPath(String p) {

        if(p == null) throw new MedusaException("Medusa: The param key is null");

        if(generatorCacheMap.containsKey(p)) {
            return generatorCacheMap.get(p);
        } else {
            return null;
        }
    }

    public static MedusaSqlGenerator putCacheGeneratorByMapperPath(String p, MedusaSqlGenerator t) {
        if(p != null && t != null) {
            return generatorCacheMap.putIfAbsent(p, t);
        } else {
            throw new MedusaException("Medusa: The param key or value is null");
        }
    }


    public static MedusaSqlGenerator getCacheGeneratorByClass(Class<?> p) {

        if(p == null) throw new MedusaException("Medusa: The param key is null");

        if(generatorSouCacheMap.containsKey(p)) {
            return generatorSouCacheMap.get(p);
        } else {
            return null;
        }
    }

    public static MedusaSqlGenerator putCacheGeneratorByClass(Class<?> p, MedusaSqlGenerator t) {
        if(p != null && t != null) {
            return generatorSouCacheMap.putIfAbsent(p, t);
        } else {
            throw new MedusaException("Medusa: The param key or value is null");
        }
    }
}
