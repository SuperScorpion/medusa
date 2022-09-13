
package com.jy.medusa.gaze.base;

import com.jy.medusa.gaze.base.select.*;

/**
 * 通用Mapper接口,基础查询
 * @param<T> 不能为空
 * Author neo
 */
public interface BaseSelectMapper<T> extends
        SelectHolyGazeMapper<T>,
        SelectHolyCountMapper<T>,
        SelectMapper<T>,
        SelectOneMapper<T>,
        SelectAllMapper<T>,
        SelectByPrimaryKeyMapper<T>,
        SelectByPrimaryKeyBatchMapper<T> {
}