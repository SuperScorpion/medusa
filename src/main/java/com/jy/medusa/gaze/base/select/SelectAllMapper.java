
package com.jy.medusa.gaze.base.select;

import com.jy.medusa.gaze.provider.BaseSelectProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * 通用Mapper接口
 * 查询表的所有的数据
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface SelectAllMapper<T> {

    /**
     * 查询表的所有的数据
     * 可以选择查询哪些列
     * @param paramColumns  可选列名称的双冒号形式的参数
     * @return              返回实体对象的list结果
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectAll")
    @ResultMap("BaseResultMap")
    List<T> selectAll(HolyGetter<T>... paramColumns);
}
