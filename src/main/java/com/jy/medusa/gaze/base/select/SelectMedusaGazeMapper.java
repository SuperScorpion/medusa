
package com.jy.medusa.gaze.base.select;

import com.jy.medusa.gaze.provider.BaseSelectProvider;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;

/**
 * 通用mapper 多条件查询
 * @param <T> 不能为空
 * @author neo
 */
public interface SelectMedusaGazeMapper<T> {

    /**
     * 根据多条件查询数据 like查询 或者是 between查询   and 连接各条件
     * @param mixParams      参数
     * @return 返回值类型
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectMedusaGaze")
    @ResultMap("BaseResultMap")
    List<T> medusaGazeMagic(Object... mixParams);
}