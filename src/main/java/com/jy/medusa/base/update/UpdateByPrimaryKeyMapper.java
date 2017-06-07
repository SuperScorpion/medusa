package com.jy.medusa.base.update;

import com.jy.medusa.provider.BaseUpdateProvider;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * 通用Mapper接口,更新
 * @param <T> 不能为空
 * @author neo
 */
public interface UpdateByPrimaryKeyMapper<T> {

    /**
     * 根据主键更新实体全部字段，null值会被更新
     * @param record
     * @return
     */
    @UpdateProvider(type = BaseUpdateProvider.class, method = "updateByPrimaryKey")
    int updateByPrimaryKey(T record, Object... ps);
}