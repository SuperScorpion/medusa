
package com.jy.medusa.gaze.base.select;

import com.jy.medusa.gaze.provider.BaseSelectProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * 通用Mapper接口,查询
 * @param <T> 不能为空
 * Author neo
 */
public interface SelectMapper<T> {

    /**
     * 根据实体中的属性值进行查询，查询条件使用等号
     * @param record      参数
     * @param paramColumns 参数
     * @return 返回值类型
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "select")
    @ResultMap("BaseResultMap")
    List<T> select(T record, HolyGetter<T>... paramColumns);
}