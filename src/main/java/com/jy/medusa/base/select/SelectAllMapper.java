
package com.jy.medusa.base.select;

import com.jy.medusa.provider.BaseSelectProvider;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 *
 * Author neo
 */
public interface SelectAllMapper<T> {

    /**
     * 查询全部结果
     * @param params 参数
     * @return 返回值类型
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectAll")
    @ResultMap("BaseResultMap")
    List<T> selectAll(Object... params);
}
