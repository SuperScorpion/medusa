
package com.jy.medusa.gaze.base.select;

import com.jy.medusa.gaze.provider.BaseSelectProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;

/**
 * 通用Mapper接口
 * 根据主键字段进行查询，方法参数必须包含完整的主键属性
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface SelectByPrimaryKeyMapper<T> {

    /**
     * 根据主键字段进行查询，方法参数必须包含完整的主键属性
     * @param pk            主键参数
     * @param paramColumns  可选列名称的双冒号形式的参数
     * @return              返回实体对象的结果
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectByPrimaryKey")
    @ResultMap("BaseResultMap")
    T selectByPrimaryKey(Serializable pk, HolyGetter... paramColumns);
}