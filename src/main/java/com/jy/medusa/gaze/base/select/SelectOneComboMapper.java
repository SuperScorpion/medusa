
package com.jy.medusa.gaze.base.select;

import com.jy.medusa.gaze.provider.BaseSelectProvider;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;

/**
 * 通用Mapper接口
 * 根据实体中的非空属性进行查询，只有一个返回值
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface SelectOneComboMapper<T> {

    /**
     * 根据实体中的非空属性进行查询，只有一个返回值 通过limit 0,1实现
     * @param mixParams 请参考{@link com.jy.medusa.gaze.base.select.SelectMedusaComboMapper}
     * @return              返回实体对象的结果
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectOneCombo")
    @ResultMap("BaseResultMap")
    T selectOneCombo(Serializable... mixParams);
}