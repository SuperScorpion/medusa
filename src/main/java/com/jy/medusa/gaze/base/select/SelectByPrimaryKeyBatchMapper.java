
package com.jy.medusa.gaze.base.select;

import com.jy.medusa.gaze.provider.BaseSelectProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;

/**
 * 通用Mapper接口
 * 根据主键的list查询结果集
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface SelectByPrimaryKeyBatchMapper<T> {

    /**
     * 根据主键的list查询结果集
     * @param pks           主键list参数
     * @param paramColumns  可选列名称的双冒号形式的参数
     * @return              返回实体对象的list结果
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectByPrimaryKeyBatch")
    @ResultMap("BaseResultMap")
    List<T> selectByPrimaryKeyBatch(List<Serializable> pks, HolyGetter<T>... paramColumns);
}