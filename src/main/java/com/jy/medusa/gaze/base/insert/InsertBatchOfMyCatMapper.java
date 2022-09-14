
package com.jy.medusa.gaze.base.insert;

import com.jy.medusa.gaze.provider.BaseInsertProvider;
import com.jy.medusa.gaze.utils.SystemConfigs;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;

import java.util.List;

/**
 * 通用Mapper接口 批插入 功能
 * @param <T> 不能为空
 * Author neo
 */
public interface InsertBatchOfMyCatMapper<T> {

    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值
     * @param records       参数
     * @param mycatSeq       参数
     * @param paramColumns       参数
     * @return 返回值类型
     */
    @InsertProvider(type = BaseInsertProvider.class, method = "insertBatchOfMyCat")
    @Options(useGeneratedKeys = true, keyProperty = "param1." + SystemConfigs.PRIMARY_KEY)///for mybatis 3.5.3 & modify by neo on 2020.01.20
    int insertBatchOfMyCat(List<T> records, String mycatSeq, Boolean isExclude, String... paramColumns);
}