
package com.jy.medusa.base;

import com.jy.medusa.base.select.*;

/**
 * 通用Mapper接口,基础查询
 * @param <T> 不能为空
 * @author neo
 */
public interface BaseSelectMapper<T> extends
        SelectOneMapper<T>,
        SelectMapper<T>,
        SelectAllMapper<T>,
        SelectCountMapper<T>,
        SelectByPrimaryKeyMapper<T>,
        SelectMedusaGazeMapper<T>,
        SelectByPrimaryKeyBatchMapper<T> {
}