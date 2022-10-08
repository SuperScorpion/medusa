package com.jy.medusa.gaze.base.update;

import com.jy.medusa.gaze.provider.BaseUpdateProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * 通用Mapper接口,更新
 * @param <T> 不能为空
 * Author neo
 */
public interface UpdateByPrimaryKeyMapper<T> {

    /**
     * 根据主键更新实体全部字段，null值会被更新
     * @param record    参数
     * @param paramColumns    参数
     * @return 返回值类型
     */
    @UpdateProvider(type = BaseUpdateProvider.class, method = "updateByPrimaryKey")
    int updateByPrimaryKey(T record, HolyGetter<T>... paramColumns);
}