
package com.jy.medusa.gaze.base;

import com.jy.medusa.gaze.base.select.*;

/**
 * 通用Mapper接口
 * 基础查询mapper类
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface BaseSelectMapper<T> extends
        SelectByPrimaryKeyMapper<T>,
        SelectByPrimaryKeyBatchMapper<T>,
        SelectAllMapper<T>,
//        SelectMapper<T>,
        SelectOneComboMapper<T>,
        SelectCountComboMapper<T>,
        SelectMedusaComboMapper<T> {
}