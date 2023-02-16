package com.jy.medusa.gaze.base.update;

import com.jy.medusa.gaze.provider.BaseUpdateProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

/**
 * 通用Mapper接口
 * 批量更新实体
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface UpdateByPrimaryKeyBatchMapper<T> {

    /**
     * 批量更新实体
     * tips: 不会过滤null值 根据主键更新实体全部字段
     *       也可以通过 isExclude参数和paramColumns参数做列名排除或包含
     * @param records       实体类list参数
     * @param isExclude     为空则插入列是所有的表字段
     *                      为true时 必须带paramColumns参数 表示排除这些可选列
     *                      为false时 必须带paramColumns参数 表示保存这些可选列
     * @param paramColumns  可选列名称的双冒号形式的参数
     * @return              更新的总共条数
     */
    @UpdateProvider(type = BaseUpdateProvider.class, method = "updateByPrimaryKeyBatch")
    int updateByPrimaryKeyBatch(List<T> records, Boolean isExclude, HolyGetter<T>... paramColumns);
}