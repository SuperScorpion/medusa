package com.jy.medusa.gaze.stuff.param.lambda;

/**
 * Created by SuperScorpion on 2020/4/21.
 */

import com.jy.medusa.generator.MedusaGenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HolyGetPropertyNameLambda {

    private static final Logger logger = LoggerFactory.getLogger(HolyGetPropertyNameLambda.class);

    /**
     * 2020/4/23 热缓存增加反射效率
     * 在不同位置同样的medusa方法里 lambda参数的class不一样 比如 Users::getName 在不同medusa方法括号里时 获取到的class是不一样的
     * 这里使用弱引用value 考虑到medusa方法越多 缓存越大 但是很多冷数据不需要一直缓存 让gc自动回收
     * 弱引用:具有弱引用的对象拥有更短暂的生命周期。如果一个对象只有弱引用存在了，则下次GC将会回收掉该对象（不管当前内存空间足够与否）
     */
    private static final Map<Class, WeakReference<SerializedLambda>> CLASS_LAMBDA_CACHE = new ConcurrentHashMap<>();

    /**
     * 根据getter方法取属性名称
     * @param fn 参数
     * @param <T> 泛型
     * @return 返回值
     */
    public static <T> String convertToFieldName(HolyGetter<T> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        if (lambda == null) return "";
        String methodName = lambda.getImplMethodName();
        String prefix = null;
        if (methodName.startsWith("get")) {
            prefix = "get";
        } else if (methodName.startsWith("is")) {
            prefix = "is";
        } else {
            logger.warn("Medusa: SerializedLambda里无效的getter方法: " + methodName);
            prefix = "";
        }

        //截取get/is之后的字符串并转换首字母为小写
        return MedusaGenUtils.lowcaseFirst(methodName.replaceFirst(prefix, ""));
    }

    /**
     * 根据setter方法取属性名称
     * @param fn 参数
     * @param <T> 泛型
     * @param <U> 泛型
     * @return 返回值
     */
    public static <T, U> String convertToFieldName(HolySetter<T, U> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        String methodName = lambda.getImplMethodName();
        if (!methodName.startsWith("set")) {
            logger.warn("Medusa: SerializedLambda里无效的setter方法：" + methodName);
        }
        return MedusaGenUtils.lowcaseFirst(methodName.replaceFirst("set", ""));
    }

    /**
     * 根据lambda的反射获取 SerializedLambda 对象
     * 加入缓存的策略
     * @param fn 参数
     * @return 返回值
     */
    public static SerializedLambda getSerializedLambda(Serializable fn) {
        SerializedLambda lambda = CLASS_LAMBDA_CACHE.get(fn.getClass()) == null ? null : CLASS_LAMBDA_CACHE.get(fn.getClass()).get();
        if (lambda == null) {
            try {
                Method method = fn.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda) method.invoke(fn);
                CLASS_LAMBDA_CACHE.put(fn.getClass(), new WeakReference<>(lambda));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lambda;
    }
}