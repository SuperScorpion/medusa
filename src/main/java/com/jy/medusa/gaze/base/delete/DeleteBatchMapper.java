
package com.jy.medusa.gaze.base.delete;

import com.jy.medusa.gaze.provider.BaseDeleteProvider;
import org.apache.ibatis.annotations.DeleteProvider;

import java.io.Serializable;
import java.util.List;

/**
 * 通用Mapper接口,删除
 * @param <T> 不能为空
 * Author neo
 */
public interface DeleteBatchMapper<T> {

    /**
     * 根据实体属性作为条件进行删除，查询条件使用等号
     * @param pks 参数
     * @return 返回值类型
     */
    @DeleteProvider(type = BaseDeleteProvider.class, method = "deleteBatch")
    int deleteBatch(List<Serializable> pks);
}