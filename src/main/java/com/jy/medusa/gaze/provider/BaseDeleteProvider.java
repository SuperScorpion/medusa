
package com.jy.medusa.gaze.provider;

import com.jy.medusa.gaze.stuff.MedusaSqlHelper;
import com.jy.medusa.gaze.stuff.exception.MedusaException;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.util.List;
import java.util.Map;

/**
 * BaseDeleteMapper实现类，基础方法实现类
 *
 */
public class BaseDeleteProvider {

    /**
     * 通过条件删除
     * @param m 参数
     * @return 返回值类型
     */
    public String delete(Map<String, Object> m) {
        return MedusaSqlHelper.getSqlGenerator(m).sqlOfDelete(m.get("pobj"));
    }

    /**
     * 通过主键删除
     * @param m 参数
     * @return 返回值类型
     */
    public String deleteByPrimaryKey(Map<String, Object> m) {
        return MedusaSqlHelper.getSqlGenerator(m).sqlOfDeleteByPrimaryKey();//modify by neo on 2016.11.13 m.get("pobj")
    }

    /**
     * 通过主键批量的去做删除
     * @param m 参数
     * @return 返回值类型
     * 新老版本产生的 bug fixed (DefaultSqlSession.StrictMap - MapperMethod.ParamMap) 20210113
     */
    public String deleteBatch(Map<String, Object> m) {

            List<Object> p = (List<Object>) ((MapperMethod.ParamMap) m).get("list");
            return MedusaSqlHelper.getSqlGenerator(m).sqlOfDeleteBatch(p);
    }
}
