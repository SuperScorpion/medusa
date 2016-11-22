
package com.jy.medusa.base.select;

import com.jy.medusa.provider.BaseSelectProvider;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 *
 * @author neo
 */
public interface SelectAllMapper<T> {

    /**
     * 查询全部结果
     * @return
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectAll")
    @ResultMap("BaseResultMap")
    List<T> selectAll();
}
