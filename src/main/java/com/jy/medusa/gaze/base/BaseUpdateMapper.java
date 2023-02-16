
package com.jy.medusa.gaze.base;


import com.jy.medusa.gaze.base.update.UpdateByPrimaryKeyBatchMapper;
import com.jy.medusa.gaze.base.update.UpdateByPrimaryKeyMapper;
import com.jy.medusa.gaze.base.update.UpdateByPrimaryKeySelectiveMapper;

/**
 * 通用Mapper接口
 * 基础更新mapper类
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface BaseUpdateMapper<T> extends
        UpdateByPrimaryKeySelectiveMapper<T>,
        UpdateByPrimaryKeyMapper<T>,
        UpdateByPrimaryKeyBatchMapper<T> {
}