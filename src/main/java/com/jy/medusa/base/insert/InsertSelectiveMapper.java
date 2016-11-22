
package com.jy.medusa.base.insert;

import com.jy.medusa.provider.BaseInsertProvider;
import com.jy.medusa.utils.SystemConfigs;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;

/**
 * 通用Mapper接口,插入
 * @param <T> 不能为空
 * @author neo
 */
public interface InsertSelectiveMapper<T> {

    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值
     *
     * @param record
     * @return
     */
    @InsertProvider(type = BaseInsertProvider.class, method = "insertSelective")
//    @SelectKey(statement = "select last_insert_id() as id", keyProperty = "id", keyColumn = "id", before = false, resultType = int.class)
    @Options(useGeneratedKeys = true, keyColumn = SystemConfigs.PRIMARY_KEY, keyProperty = SystemConfigs.PRIMARY_KEY)
    int insertSelective(T record);
}