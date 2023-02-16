
package com.jy.medusa.gaze.base.insert;

import com.jy.medusa.gaze.provider.BaseInsertProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import com.jy.medusa.gaze.utils.SystemConfigs;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;

import java.util.List;

/**
 * 通用Mapper接口
 * 批量保存实体
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface InsertBatchMapper<T> {

    /**
     * 批量保存实体
     * tips: 不会过滤null值 如果sql语句没有某列的名称 则该列会使用数据库该列的默认值
     *       也可以通过 isExclude参数和paramColumns参数做列名排除或包含
     * @param records       实体类list参数
     * @param isExclude     为空则插入列是所有的表字段
     *                      为true时 必须带paramColumns参数 表示排除这些可选列
     *                      为false时 必须带paramColumns参数 表示保存这些可选列
     * @param paramColumns  可选列名称的双冒号形式的参数
     * @return              保存的总共条数
     */
    @InsertProvider(type = BaseInsertProvider.class, method = "insertBatch")
//    @SelectKey(statement = "select last_insert_id() as id", keyProperty = "id", keyColumn = "id", before = true, resultType = int.class)
    @Options(useGeneratedKeys = true, keyProperty = "param1." + SystemConfigs.PRIMARY_KEY)///for mybatis 3.5.3 & not support pobj.param1 & modify by SuperScorpion on 2020.01.20
    int insertBatch(List<T> records, Boolean isExclude, HolyGetter<T>... paramColumns);
}