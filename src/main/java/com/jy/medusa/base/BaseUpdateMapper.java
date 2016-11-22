
package com.jy.medusa.base;


import com.jy.medusa.base.update.UpdateByPrimaryKeyMapper;
import com.jy.medusa.base.update.UpdateByPrimaryKeySelectiveMapper;

/**
 * 通用Mapper接口,基础查询
 *
 * @param <T> 不能为空
 * @author neo
 */
public interface BaseUpdateMapper<T> extends
        UpdateByPrimaryKeySelectiveMapper<T>,
        UpdateByPrimaryKeyMapper<T> {
}