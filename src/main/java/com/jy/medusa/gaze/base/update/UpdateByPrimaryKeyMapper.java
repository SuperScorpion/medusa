package com.jy.medusa.gaze.base.update;

import com.jy.medusa.gaze.provider.BaseUpdateProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * 通用Mapper接口
 * 根据主键单个更新 不过滤null值属性
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface UpdateByPrimaryKeyMapper<T> {

    /**
     * 根据主键单个更新 不过滤null值属性
     * @param record        实体类参数
     * @param paramColumns  可选列名称的双冒号形式的参数
     * @return              更新的总共条数
     */
    @UpdateProvider(type = BaseUpdateProvider.class, method = "updateByPrimaryKey")
    int updateByPrimaryKey(T record, HolyGetter<T>... paramColumns);
}