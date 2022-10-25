
package com.jy.medusa.gaze.base;


import com.jy.medusa.gaze.base.update.UpdateByPrimaryKeyBatchMapper;
import com.jy.medusa.gaze.base.update.UpdateByPrimaryKeyMapper;
import com.jy.medusa.gaze.base.update.UpdateByPrimaryKeySelectiveMapper;

/**
 * 通用Mapper接口,基础查询
 *
 * @param <T> 不能为空
 * @author neo
 */
public interface BaseUpdateMapper<T> extends
        UpdateByPrimaryKeySelectiveMapper<T>,
        UpdateByPrimaryKeyMapper<T>,
        UpdateByPrimaryKeyBatchMapper<T> {
}