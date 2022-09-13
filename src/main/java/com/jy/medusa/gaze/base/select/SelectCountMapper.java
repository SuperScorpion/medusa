
package com.jy.medusa.gaze.base.select;

import com.jy.medusa.gaze.provider.BaseSelectProvider;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;

/**
 * 通用Mapper接口,查询
 * @param <T> 不能为空
 * Author neo
 */
public interface SelectCountMapper<T> {

    /**
     * 根据实体中的属性查询总数，查询条件使用等号
     * @param mixParams       参数
     * @return 返回值类型
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectCount")
    int selectCount(Object... mixParams);
}