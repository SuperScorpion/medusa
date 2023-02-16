
package com.jy.medusa.gaze.base.select;

import com.jy.medusa.gaze.provider.BaseSelectProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * 通用Mapper接口
 * 根据实体中的非空属性值进行查询
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface SelectMapper<T> {

    /**
     * 根据实体中的非空属性值进行查询
     * @param record        实体类参数
     * @param paramColumns  可选列名称的双冒号形式的参数
     * @return              返回实体对象的list结果
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "select")
    @ResultMap("BaseResultMap")
    List<T> select(T record, HolyGetter<T>... paramColumns);
}