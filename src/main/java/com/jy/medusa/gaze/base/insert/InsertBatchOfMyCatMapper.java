
package com.jy.medusa.gaze.base.insert;

import com.jy.medusa.gaze.provider.BaseInsertProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import com.jy.medusa.gaze.utils.SystemConfigs;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;

import java.util.List;

/**
 * 通用Mapper接口
 * 批量保存实体 for mycat
 * @param <T> 实体类泛型
 * @author SuperScorpion
 * @deprecated 暂时弃用
 */
public interface InsertBatchOfMyCatMapper<T> {

    /**
     * 批量保存实体
     * tips: 如果sql语句没有某列的名称 则该列会使用数据库该列的默认值
     * @param records       实体类list参数
     * @param mycatSeq      myCatSequence
     * @param isExclude     为空则插入列是所有的表字段
     *                      为true时 必须带paramColumns参数 表示排除这些可选列
     *                      为false时 必须带paramColumns参数 表示保存这些可选列
     * @param paramColumns  可选列名称的双冒号形式的参数
     * @return              保存的总共条数
     */
    @InsertProvider(type = BaseInsertProvider.class, method = "insertBatchOfMyCat")
    @Options(useGeneratedKeys = true, keyProperty = "param1." + SystemConfigs.PRIMARY_KEY)///for mybatis 3.5.3 & modify by SuperScorpion on 2020.01.20
    int insertBatchOfMyCat(List<T> records, String mycatSeq, Boolean isExclude, HolyGetter<T>... paramColumns);
}