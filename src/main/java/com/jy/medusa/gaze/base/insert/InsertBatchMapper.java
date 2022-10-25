
package com.jy.medusa.gaze.base.insert;

import com.jy.medusa.gaze.provider.BaseInsertProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import com.jy.medusa.gaze.utils.SystemConfigs;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;

import java.util.List;

/**
 * 通用Mapper接口 批插入 功能
 * @param <T> 不能为空
 * @author neo
 */
public interface InsertBatchMapper<T> {

    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值
     * @param records       参数
     * @param paramColumns       参数
     * @return 返回值类型
     */
    @InsertProvider(type = BaseInsertProvider.class, method = "insertBatch")
//    @SelectKey(statement = "select last_insert_id() as id", keyProperty = "id", keyColumn = "id", before = true, resultType = int.class)
    @Options(useGeneratedKeys = true, keyProperty = "param1." + SystemConfigs.PRIMARY_KEY)///for mybatis 3.5.3 & not support pobj.param1 & modify by neo on 2020.01.20
    int insertBatch(List<T> records, Boolean isExclude, HolyGetter<T>... paramColumns);
}