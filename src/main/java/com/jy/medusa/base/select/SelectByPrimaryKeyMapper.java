
package com.jy.medusa.base.select;

import com.jy.medusa.provider.BaseSelectProvider;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * 通用Mapper接口,其他接口继承该接口即可
 * @param <T> 不能为空
 * Author neo
 */
public interface SelectByPrimaryKeyMapper<T> {

    /**
     * 根据主键字段进行查询，方法参数必须包含完整的主键属性，查询条件使用等号
     * @param key 参数
     * @param paramColumn 参数
     * @return 返回值类型
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectByPrimaryKey")
    @ResultMap("BaseResultMap")
    T selectByPrimaryKey(Object key, Object... paramColumn);
}