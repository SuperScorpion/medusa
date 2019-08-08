
package com.jy.medusa.gaze.provider;

import com.jy.medusa.gaze.stuff.MyHelper;
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
        return MyHelper.getSqlGenerator(m).sqlOfRemoveByCondition(m.get("pobj"));
    }

    /**
     * 通过主键删除
     * @param m 参数
     * @return 返回值类型
     */
    public String deleteByPrimaryKey(Map<String, Object> m) {
        return MyHelper.getSqlGenerator(m).sqlOfRemoveById();//modify by neo on 2016.11.13 m.get("pobj")
    }

    /**
     * 通过主键批量的去做删除
     * @param m 参数
     * @return 返回值类型
     */
    public String deleteBatch(Map<String, Object> m) {

        List<Object> p = (List<Object>) ((DefaultSqlSession.StrictMap) m.get("pobj")).get("list");

        return MyHelper.getSqlGenerator(m).sqlOfRemoveOfBatch(p);
    }
}
