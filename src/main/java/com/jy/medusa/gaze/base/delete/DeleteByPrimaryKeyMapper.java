
package com.jy.medusa.gaze.base.delete;

import com.jy.medusa.gaze.provider.BaseDeleteProvider;
import org.apache.ibatis.annotations.DeleteProvider;

/**
 * 通用Mapper接口,删除
 * @param <T> 不能为空
 * Author neo
 */
public interface DeleteByPrimaryKeyMapper<T> {

    /**
     * 根据主键字段进行删除，方法参数必须包含完整的主键属性
     * @param pk 参数
     * @return 返回值类型
     */
    @DeleteProvider(type = BaseDeleteProvider.class, method = "deleteByPrimaryKey")
    int deleteByPrimaryKey(Object pk);
}