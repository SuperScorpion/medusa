
package com.jy.medusa.gaze.base.insert;

import com.jy.medusa.gaze.provider.BaseInsertProvider;
import com.jy.medusa.gaze.utils.SystemConfigs;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectKey;

/**
 * 通用Mapper接口,插入 UUID 主键
 * @param <T> 不能为空
 * @author neo
 */
public interface InsertSelectiveUUIDMapper<T> {

    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值
     * @param record    参数
     * @return 返回值类型
     */
    @InsertProvider(type = BaseInsertProvider.class, method = "insertSelective")
    @SelectKey(statement = "SELECT REPLACE(UUID(), '-', '')", keyProperty = SystemConfigs.PRIMARY_KEY, before = true, resultType = String.class)
    int insertSelectiveUUID(T record);
}