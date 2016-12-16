
package com.jy.medusa.base;

import com.jy.medusa.base.insert.InsertBatchMapper;
import com.jy.medusa.base.insert.InsertSelectiveMapper;

/**
 * 通用Mapper接口,基础查询
 * @param <T> 不能为空
 * @author neo
 */
public interface BaseInsertMapper<T> extends
        InsertSelectiveMapper<T>,
        InsertBatchMapper<T> {
}