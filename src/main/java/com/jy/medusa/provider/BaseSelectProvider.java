
package com.jy.medusa.provider;

import com.jy.medusa.stuff.MyHelper;
import com.jy.medusa.stuff.Pager;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.util.Map;

/**
 * BaseSelectProvider实现类，基础方法实现类
 * @author neo
 */
public class BaseSelectProvider {


    /**
     * 可根据条件查询出一个记录
     * @return
     */
    public String selectOne(Map<String, Object> m) {

//        return "SELECT * FROM  users where NAME = #{pobj.param1.name} limit 0,1";

        if(m.get("pobj") instanceof MapperMethod.ParamMap)
            return MyHelper.getSqlGenerator(m).sql_findOne(
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param1"),
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param2"));
        else
            return MyHelper.getSqlGenerator(m).sql_findOne(m.get("pobj"));
    }

    /**
     * 查询
     * @return
     */
    public String select(Map<String, Object> m) {

        if(m.get("pobj") instanceof MapperMethod.ParamMap)
            return MyHelper.getSqlGenerator(m).sql_findListBy(
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param1"),
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param2"));
        else
            return MyHelper.getSqlGenerator(m).sql_findListBy(m.get("pobj"));
    }

    /**
     * 根据主键进行查询
     */
    public String selectByPrimaryKey(Map<String, Object> m) {

        if(m.get("pobj") instanceof MapperMethod.ParamMap)
            return MyHelper.getSqlGenerator(m).sql_findOneById(
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param1"),
                    ((MapperMethod.ParamMap) m.get("pobj")).get("param2"));
        else
            return MyHelper.getSqlGenerator(m).sql_findOneById(m.get("pobj"));
    }



    /**
     * 查询全部结果
     * @return
     */
    public String selectAll(Map<String, Object> m) {

        return MyHelper.getSqlGenerator(m).sql_findAll();
    }



    /**
     * 查询总数
     * @return
     */
    public String selectCount(Map<String, Object> m) {

        if(m.get("pobj") instanceof DefaultSqlSession.StrictMap)
            return MyHelper.getSqlGenerator(m).sql_findAllCount((Object[]) ((DefaultSqlSession.StrictMap) m.get("pobj")).get("array"));

        throw new RuntimeException("selectCount DefaultSqlSession.StrictMap Exception");
    }



    /**
     * 根据多条件查询数据
     * @return
     */
    public String selectMedusaGaze(Map<String, Object> m) {

        if(m.get("pobj") instanceof DefaultSqlSession.StrictMap)
            return MyHelper.getSqlGenerator(m).sql_findMedusaGaze((Object[]) ((DefaultSqlSession.StrictMap) m.get("pobj")).get("array"));

        throw new RuntimeException("selectMedusaGaze DefaultSqlSession.StrictMap Exception");
    }
}
