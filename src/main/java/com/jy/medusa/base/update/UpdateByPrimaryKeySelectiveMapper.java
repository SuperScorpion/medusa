package com.jy.medusa.base.update;

import com.jy.medusa.provider.BaseUpdateProvider;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * 通用Mapper接口,更新
 * @param <T> 不能为空
 * @author neo
 */
public interface UpdateByPrimaryKeySelectiveMapper<T> {

    /**
     * 根据主键更新属性不为null的值
     * @param record
     * @return
     */
    @UpdateProvider(type = BaseUpdateProvider.class, method = "updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(T record);

}