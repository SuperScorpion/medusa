
package com.jy.medusa.gaze.base.delete;

import com.jy.medusa.gaze.provider.BaseDeleteProvider;
import org.apache.ibatis.annotations.DeleteProvider;

import java.io.Serializable;

/**
 * 通用Mapper接口
 * 条件删除 根据实体非null属性作为条件进行删除
 * @param <T> 实体类泛型
 * @author SuperScorpion
 */
public interface DeleteMedusaComboMapper<T> {

    /**
     * 条件删除         根据实体非null属性作为条件进行删除
     * @param mixParams 请参考{@link com.jy.medusa.gaze.base.select.SelectMedusaComboMapper}
     * @return         删除的总共条数
     */
    @DeleteProvider(type = BaseDeleteProvider.class, method = "deleteMedusaCombo")
    int deleteMedusaCombo(Serializable... mixParams);
}