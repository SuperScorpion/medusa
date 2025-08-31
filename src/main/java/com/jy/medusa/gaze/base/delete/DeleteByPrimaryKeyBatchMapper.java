
package com.jy.medusa.gaze.base.delete;

import com.jy.medusa.gaze.provider.BaseDeleteProvider;
import org.apache.ibatis.annotations.DeleteProvider;

import java.io.Serializable;
import java.util.List;

/**
 * 通用Mapper接口
 * 批量删除 根据主键的list集合做删除操作
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface DeleteByPrimaryKeyBatchMapper<T> {

    /**
     * 批量删除     根据主键的list集合做删除操作
     * @param pks  主键的list集合参数
     * @return     删除的总共条数
     */
    @DeleteProvider(type = BaseDeleteProvider.class, method = "deleteBatch")
    int deleteByPrimaryKeyBatch(List<? extends Serializable> pks);
}