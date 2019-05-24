
package com.jy.medusa.gaze.base;

import com.jy.medusa.gaze.base.insert.InsertBatchMapper;
import com.jy.medusa.gaze.base.insert.InsertMapper;
import com.jy.medusa.gaze.base.insert.InsertSelectiveMapper;
import com.jy.medusa.gaze.base.insert.InsertSelectiveUUIDMapper;

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