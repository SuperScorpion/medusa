
package com.jy.medusa.gaze.base.insert;

import com.jy.medusa.gaze.provider.BaseInsertProvider;
import com.jy.medusa.gaze.utils.SystemConfigs;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;

/**
 * 通用Mapper接口
 * 保存单个实体类，null的属性值会被过滤，如果有默认值则会使用数据库默认值
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface InsertSelectiveMapper<T> {

    /**
     * 保存单个实体类，null的属性值会被过滤，如果有默认值则会使用数据库默认值
     * @param record  实体类参数
     * @return        保存的条数
     */
    @InsertProvider(type = BaseInsertProvider.class, method = "insertSelective")
    @Options(useGeneratedKeys = true, keyProperty = SystemConfigs.PRIMARY_KEY)
    int insertSelective(T record);
}