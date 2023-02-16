
package com.jy.medusa.gaze.base;

import com.jy.medusa.gaze.base.insert.*;

/**
 * 通用Mapper接口
 * 基础保存mapper类
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface BaseInsertMapper<T> extends
        InsertSelectiveMapper<T>,
        InsertBatchMapper<T>,
        InsertBatchOfMyCatMapper<T>,
        InsertSelectiveUUIDMapper<T>,
        InsertMapper<T> {
}