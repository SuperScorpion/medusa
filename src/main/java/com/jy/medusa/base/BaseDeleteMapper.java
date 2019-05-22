
package com.jy.medusa.base;

import com.jy.medusa.base.delete.DeleteBatchMapper;
import com.jy.medusa.base.delete.DeleteByPrimaryKeyMapper;
import com.jy.medusa.base.delete.DeleteMapper;

/**
 * 通用Mapper接口,基础删除
 * @param <T> 不能为空
 * Author neo
 */
public interface BaseDeleteMapper<T> extends
        DeleteMapper<T>,
        DeleteByPrimaryKeyMapper<T>,
        DeleteBatchMapper {
}