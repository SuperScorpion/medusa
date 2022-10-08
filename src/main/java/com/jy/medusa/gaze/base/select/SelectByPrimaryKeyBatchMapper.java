
package com.jy.medusa.gaze.base.select;

import com.jy.medusa.gaze.provider.BaseSelectProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;

/**
 * 通用Mapper接口,查询
 * @param <T> 不能为空
 * Author neo
 */
public interface SelectByPrimaryKeyBatchMapper<T> {

    /**
     * 根据ids查询
     * @param pks        参数
     * @param paramColumns        参数
     * @return 返回值类型
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectByPrimaryKeyBatch")
    @ResultMap("BaseResultMap")
    List<T> selectByPrimaryKeyBatch(List<Serializable> pks, HolyGetter<T>... paramColumns);
}