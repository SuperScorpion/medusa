
package com.jy.medusa.base.select;

import com.jy.medusa.provider.BaseSelectProvider;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * 通用Mapper接口,查询
 *
 * @param <T> 不能为空
 * @author neo
 */
public interface SelectMapper<T> {

    /**
     * 根据实体中的属性值进行查询，查询条件使用等号
     * @param record
     * @return
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "select")
    @ResultMap("BaseResultMap")
    List<T> select(T record, Object... paramColumn);

}