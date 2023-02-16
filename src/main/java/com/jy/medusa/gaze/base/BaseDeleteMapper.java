
package com.jy.medusa.gaze.base;

import com.jy.medusa.gaze.base.delete.DeleteBatchMapper;
import com.jy.medusa.gaze.base.delete.DeleteByPrimaryKeyMapper;
import com.jy.medusa.gaze.base.delete.DeleteMapper;

/**
 * 通用Mapper接口
 * 基础删除mapper类
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface BaseDeleteMapper<T> extends
        DeleteMapper<T>,
        DeleteByPrimaryKeyMapper<T>,
        DeleteBatchMapper<T> {
}