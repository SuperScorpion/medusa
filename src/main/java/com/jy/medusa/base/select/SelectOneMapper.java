
package com.jy.medusa.base.select;

import com.jy.medusa.provider.BaseSelectProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * 通用Mapper接口,查询
 * @param <T> 不能为空
 * @author neo
 */
public interface SelectOneMapper<T> {

    /**
     * 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     * @param record
     * @return
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectOne")
    @ResultMap("BaseResultMap")
    T selectOne(T record, Object... paramColumn);
}