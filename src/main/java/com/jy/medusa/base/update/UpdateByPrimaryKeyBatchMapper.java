package com.jy.medusa.base.update;

import com.jy.medusa.provider.BaseUpdateProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

/**
 * 通用Mapper接口,更新批量
 * @param <T> 不能为空
 * @author neo
 */
public interface UpdateByPrimaryKeyBatchMapper<T> {

    /**
     * 根据主键更新实体全部字段，null值会被更新
     * @param
     * @return
     */
    @UpdateProvider(type = BaseUpdateProvider.class, method = "updateByPrimaryKeyBatch")
    int updateByPrimaryKeyBatch(List<T> records, Object... ps);
}