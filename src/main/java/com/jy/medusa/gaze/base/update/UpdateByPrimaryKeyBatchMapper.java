package com.jy.medusa.gaze.base.update;

import com.jy.medusa.gaze.provider.BaseUpdateProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

/**
 * 通用Mapper接口,更新批量
 * @param <T> 不能为空
 * Author neo
 */
public interface UpdateByPrimaryKeyBatchMapper<T> {

    /**
     * 根据主键更新实体全部字段，null值会被更新
     * @param records 参数
     * @param paramColumns 参数
     * @return 返回值类型
     */
    @UpdateProvider(type = BaseUpdateProvider.class, method = "updateByPrimaryKeyBatch")
    int updateByPrimaryKeyBatch(List<T> records, Boolean isExclude, String... paramColumns);
}