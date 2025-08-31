
package com.jy.medusa.gaze.base.select;

import com.jy.medusa.gaze.provider.BaseSelectProvider;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;

/**
 * 这是个大招 万能查询方法 根据多条件查询数据条数
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface SelectCountComboMapper<T> {

    /**
     * 这是个大招 万能查询方法 根据多条件查询数据条数 请参考{@link com.jy.medusa.gaze.base.select.SelectMedusaComboMapper}
     * @param mixParams 请参考{@link com.jy.medusa.gaze.base.select.SelectMedusaComboMapper}
     * @return              返回查询出的总条数
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "selectCountCombo")
    int selectCountCombo(Serializable... mixParams);
}