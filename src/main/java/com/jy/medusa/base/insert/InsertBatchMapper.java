
package com.jy.medusa.base.insert;

import com.jy.medusa.provider.BaseInsertProvider;
import com.jy.medusa.utils.SystemConfigs;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;

import java.util.List;

/**
 * 通用Mapper接口 批插入 功能
 * @param <T> 不能为空
 * @author neo
 */
public interface InsertBatchMapper<T> {

    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值
     * @param record
     * @return
     */
    @InsertProvider(type = BaseInsertProvider.class, method = "insertBatch")
//    @SelectKey(statement = "select last_insert_id() as id", keyProperty = "id", keyColumn = "id", before = false, resultType = int.class)
    @Options(useGeneratedKeys = true, keyColumn = SystemConfigs.PRIMARY_KEY, keyProperty = SystemConfigs.PRIMARY_KEY)
    int insertBatch(List<T> record);
}