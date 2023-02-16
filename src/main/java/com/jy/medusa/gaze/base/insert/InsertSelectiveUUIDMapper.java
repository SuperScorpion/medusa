
package com.jy.medusa.gaze.base.insert;

import com.jy.medusa.gaze.provider.BaseInsertProvider;
import com.jy.medusa.gaze.utils.SystemConfigs;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectKey;

/**
 * 通用Mapper接口
 * 保存单个实体类 生成UUID主键(已经过滤横杠)
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface InsertSelectiveUUIDMapper<T> {

    /**
     * 保存单个实体类 生成UUID主键(已经过滤横杠)
     * @param record  实体类参数
     * @return        保存的条数
     */
    @InsertProvider(type = BaseInsertProvider.class, method = "insertSelective")
    @SelectKey(statement = "SELECT REPLACE(UUID(), '-', '')", keyProperty = SystemConfigs.PRIMARY_KEY, before = true, resultType = String.class)
    int insertSelectiveUUID(T record);
}