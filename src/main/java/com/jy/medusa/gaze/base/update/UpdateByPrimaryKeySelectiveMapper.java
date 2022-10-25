package com.jy.medusa.gaze.base.update;

import com.jy.medusa.gaze.provider.BaseUpdateProvider;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * 通用Mapper接口,更新
 * @param <T> 不能为空
 * @author neo
 */
public interface UpdateByPrimaryKeySelectiveMapper<T> {

    /**
     * 根据主键更新属性不为null的值
     * @param record   参数
     * @return 返回值类型
     */
    @UpdateProvider(type = BaseUpdateProvider.class, method = "updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(T record);

}