
package com.jy.medusa.base;

import com.jy.medusa.base.insert.InsertBatchMapper;
import com.jy.medusa.base.insert.InsertMapper;
import com.jy.medusa.base.insert.InsertSelectiveMapper;
import com.jy.medusa.base.insert.InsertSelectiveUUIDMapper;

/**
 * 通用Mapper接口,基础查询
 * @param <T> 不能为空
 * Author neo
 */
public interface BaseInsertMapper<T> extends
        InsertSelectiveMapper<T>,
        InsertBatchMapper<T>,
        InsertSelectiveUUIDMapper,
        InsertMapper<T> {
}