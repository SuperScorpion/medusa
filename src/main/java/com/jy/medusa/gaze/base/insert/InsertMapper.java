
package com.jy.medusa.gaze.base.insert;

import com.jy.medusa.gaze.provider.BaseInsertProvider;
import com.jy.medusa.gaze.utils.SystemConfigs;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;

/**
 * 通用Mapper接口,插入
 * @param <T> 不能为空
 * Author neo
 */
public interface InsertMapper<T> {

    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值
     * @param record     参数
     * @return 返回值类型
     */
    @InsertProvider(type = BaseInsertProvider.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = SystemConfigs.PRIMARY_KEY)
    int insert(T record);
}