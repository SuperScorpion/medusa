
package com.jy.medusa.gaze.base.delete;

import com.jy.medusa.gaze.provider.BaseDeleteProvider;
import org.apache.ibatis.annotations.DeleteProvider;

import java.io.Serializable;

/**
 * 通用Mapper接口
 * 单一删除 根据主键做删除操作
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface DeleteByPrimaryKeyMapper<T> {

    /**
     * 单一删除     根据主键做删除操作
     * @param pk   主键参数
     * @return     删除的总共条数
     */
    @DeleteProvider(type = BaseDeleteProvider.class, method = "deleteByPrimaryKey")
    int deleteByPrimaryKey(Serializable pk);
}