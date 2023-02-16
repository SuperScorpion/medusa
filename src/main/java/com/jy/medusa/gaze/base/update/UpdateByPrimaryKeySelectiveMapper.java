package com.jy.medusa.gaze.base.update;

import com.jy.medusa.gaze.provider.BaseUpdateProvider;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * 通用Mapper接口
 * 根据主键单个更新 过滤null值属性
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface UpdateByPrimaryKeySelectiveMapper<T> {

    /**
     * 根据主键单个更新 过滤null值属性
     * @param record        实体类参数
     * @return              更新的总共条数
     */
    @UpdateProvider(type = BaseUpdateProvider.class, method = "updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(T record);

}