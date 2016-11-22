
package com.jy.medusa.base.delete;

import com.jy.medusa.provider.BaseDeleteProvider;
import org.apache.ibatis.annotations.DeleteProvider;

import java.util.List;

/**
 * 通用Mapper接口,删除
 * @param <T> 不能为空
 * @author neo
 */
public interface DeleteBatchMapper<T> {

    /**
     * 根据实体属性作为条件进行删除，查询条件使用等号
     * @return
     */
    @DeleteProvider(type = BaseDeleteProvider.class, method = "deleteBatch")
    int deleteBatch(List<Integer> ds);
}