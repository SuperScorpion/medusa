
package com.jy.medusa.base.select;

import com.jy.medusa.provider.BaseSelectProvider;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * 通用Mapper接口,查询
 * @param <T> 不能为空
 * @author neo
 */
public interface SelectByIdsMapper<T> {

    /**
     * 根据ids查询
     * @param ds
     * @return
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectByIds")
    @ResultMap("BaseResultMap")
    List<T> selectByIds(List<Integer> ds, Object... paramColumn);
}