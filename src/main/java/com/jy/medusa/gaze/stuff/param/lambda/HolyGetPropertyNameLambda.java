package com.jy.medusa.gaze.stuff.param.lambda;

/**
 * Created by neo on 2020/4/21.
 */

import com.jy.medusa.generator.MedusaGenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public class HolyGetPropertyNameLambda {

    private static final Logger logger = LoggerFactory.getLogger(HolyGetPropertyNameLambda.class);

    //// TODO: 2020/4/23 由于 Class 每次产生的不一样 所以缓存的没有意义
//    private static Map<Class, WeakReference<SerializedLambda>> CLASS_LAMBDA_CACHE = new ConcurrentHashMap<>();

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
        if(methodName.startsWith("get")){
            prefix = "get";
        }
        else if(methodName.startsWith("is")){
            prefix = "is";
        }
        if(prefix == null){
            logger.warn("Medusa: SerializedLambda里无效的getter方法: " + methodName);
            prefix = "";
        }

        //截取get/is之后的字符串并转换首字母为小写
        return MedusaGenUtils.lowcaseFirst(methodName.replace(prefix, ""));
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
        if(!methodName.startsWith("set")) {
            logger.warn("Medusa: SerializedLambda里无效的setter方法：" + methodName);
        }
        return MedusaGenUtils.lowcaseFirst(methodName.replace("set", ""));
    }

    /**
     * 根据lambda的反射获取 SerializedLambda 对象
     * 加入缓存的策略
     * @param fn 参数
     * @return 返回值
     */
    public static SerializedLambda getSerializedLambda(Serializable fn) {
//        SerializedLambda lambda = CLASS_LAMBDA_CACHE.get(fn.getClass()).get();
//        if(lambda == null) {
        SerializedLambda lambda = null;
        if (fn != null) {
            try {
                Method method = fn.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda) method.invoke(fn);
//                CLASS_LAMBDA_CACHE.put(fn.getClass(), new WeakReference<>(lambda));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        }
        return lambda;
    }
}