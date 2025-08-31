
package com.jy.medusa.gaze.base;

import com.jy.medusa.gaze.base.delete.DeleteByPrimaryKeyBatchMapper;
import com.jy.medusa.gaze.base.delete.DeleteByPrimaryKeyMapper;
import com.jy.medusa.gaze.base.delete.DeleteMedusaComboMapper;

/**
 * 通用Mapper接口
 * 基础删除mapper类
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface BaseDeleteMapper<T> extends
        DeleteMedusaComboMapper<T>,
        DeleteByPrimaryKeyMapper<T>,
        DeleteByPrimaryKeyBatchMapper<T> {
}