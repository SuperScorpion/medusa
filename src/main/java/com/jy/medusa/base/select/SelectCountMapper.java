
package com.jy.medusa.base.select;

import com.jy.medusa.provider.BaseSelectProvider;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * 通用Mapper接口,查询
 * @param <T> 不能为空
 * @author neo
 */
public interface SelectCountMapper<T> {

    /**
     * 根据实体中的属性查询总数，查询条件使用等号
     * @param params
     * @return
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectCount")
    int selectCount(Object... params);
}