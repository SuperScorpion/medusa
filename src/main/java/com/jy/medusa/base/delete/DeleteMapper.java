
package com.jy.medusa.base.delete;

import com.jy.medusa.provider.BaseDeleteProvider;
import org.apache.ibatis.annotations.DeleteProvider;

/**
 * 通用Mapper接口,删除
 * @param <T> 不能为空
 * @author neo
 */
public interface DeleteMapper<T> {

    /**
     * 根据实体属性作为条件进行删除，查询条件使用等号
     * @param record
     * @return
     */
    @DeleteProvider(type = BaseDeleteProvider.class, method = "delete")
    int delete(T record);

}