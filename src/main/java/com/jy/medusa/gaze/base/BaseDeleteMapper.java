
package com.jy.medusa.gaze.base;

import com.jy.medusa.gaze.base.delete.DeleteBatchMapper;
import com.jy.medusa.gaze.base.delete.DeleteByPrimaryKeyMapper;
import com.jy.medusa.gaze.base.delete.DeleteMapper;

/**
 * 通用Mapper接口,基础删除
 * @param <T> 不能为空
 * Author neo
 */
public interface BaseDeleteMapper<T> extends
        DeleteMapper<T>,
        DeleteByPrimaryKeyMapper<T>,
        DeleteBatchMapper<T> {
}