
package com.jy.medusa.gaze.base.select;

import com.jy.medusa.gaze.provider.BaseSelectProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * 通用Mapper接口
 * 根据实体中的非空属性进行查询，只有一个返回值
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface SelectOneMapper<T> {

    /**
     * 根据实体中的非空属性进行查询，只有一个返回值 通过limit 0,1实现
     * @param record        实体类参数
     * @param paramColumns  可选列名称的双冒号形式的参数
     * @return              返回实体对象的结果
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectOne")
    @ResultMap("BaseResultMap")
    T selectOne(T record, HolyGetter<T>... paramColumns);
}