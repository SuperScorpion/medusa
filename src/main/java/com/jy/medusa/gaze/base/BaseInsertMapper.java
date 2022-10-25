
package com.jy.medusa.gaze.base;

import com.jy.medusa.gaze.base.insert.*;

/**
 * 通用Mapper接口,基础查询
 * @param <T> 不能为空
 * @author neo
 */
public interface BaseInsertMapper<T> extends
        InsertSelectiveMapper<T>,
        InsertBatchMapper<T>,
        InsertBatchOfMyCatMapper<T>,
        InsertSelectiveUUIDMapper<T>,
        InsertMapper<T> {
}